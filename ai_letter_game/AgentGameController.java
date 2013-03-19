package ai_letter_game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import ai_letter_game.AgentPlayer.waitCFPBehaviour;
import ai_letter_game.AgentPlayer.waitTurnBehaviour;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

@SuppressWarnings("serial")
public class AgentGameController extends MockAgent {
	
	// this is the GUI reference
	private LetterGameGui myGui = null;
	
	// for the player agents
	private AgentContainer container = null;
	private AgentController[] acPlayers;

	// game levels
	private final int LEVEL1 = 1;
	private final int LEVEL2 = 2;
	private final int LEVEL3 = 3;
	private final int LEVEL2_SCORE = 10;
	private final int LEVEL3_SCORE = 50;
	private final int FINAL_SCORE = 200;
	
	// current player
	private int currentPlayer;
	private final int maxPlayers = 15;
	
	// game state
	private final int startMoney = 10;
	protected OneShotBehaviour currentBehaviour;
	private int levels[];
	private int credits[];
	private boolean isPlaying[];
	private String requestLetter;
	String[] simpleGoalWords = {
							"cold", "mind", "fire", "word",
							"blind", "stuff", "upset", "freak",
							"hustle", "winter", "around", "listen"
						 };
	String[] goalWords;
	String[] startingLetters;
	
	public AgentGameController() {
		this.serviceDescriptionType = "controller" + hashCode();
		this.serviceDescriptionName = "game-controller" + hashCode();

		// Set up the gui
		myGui = new LetterGameGui(this);
		myGui.setVisible(true);
		
		// Initialize the state machine
		currentPlayer = 1;
	}

	@Override
	protected void setup() {
		super.setup();

		// Get the Container
		container = getContainerController();
	}

	public void startGame() {
		currentBehaviour = null;

		// UI updates
		// switch enabled action buttons
		myGui.btnStartGame.setEnabled(false);
		myGui.comboPlayer1.setEnabled(false);
		myGui.comboPlayer2.setEnabled(false);
		myGui.comboPlayer3.setEnabled(false);
		myGui.comboPlayer4.setEnabled(false);
		myGui.btnStopGame.setEnabled(true);

		// create the players
		try {
			// holders for agents and game state
			acPlayers 	= new AgentController[maxPlayers];
			levels		= new int[maxPlayers];
			credits		= new int[maxPlayers];
			isPlaying 	= new boolean[maxPlayers];

			for(int i=0; i < maxPlayers; i++) {
				// create AgentPlayer
				acPlayers[i] = container.createNewAgent("Player"+(i+1), "ai_letter_game.AgentPlayer", null);
				acPlayers[i].start();

				// init game state
				levels[i]	 = LEVEL1;
				credits[i]	 = startMoney;
				isPlaying[i] = true;

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		consoleLog("waiting for players to start..");
		// arranging little delay, until the player agents are loaded
		try {
			Thread.sleep(3000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		consoleLog("sending game start information to all players");
		goalWords = getGoalWords();
		startingLetters = getStartingLetters(goalWords);
		try {
			for(int i=0; i<maxPlayers; i++) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));
				// starting credits, one word per level (4, 5, 6 chars), starting letters, is it their turn?
				msg.setContent(startMoney+";"+goalWords[i]+";"+goalWords[i+maxPlayers]+";"+goalWords[i+maxPlayers]+";"+startingLetters[i]+";"+(currentPlayer == i));
				send(msg);
				
				String ui = "Player "+ (i+1) + " ["+ credits[i] +"] " + goalWords[i];
				switch (i) {
					case 0:
						myGui.txtInfoPlayer1.setText(ui);	
						break;
					case 1:
						myGui.txtInfoPlayer2.setText(ui);
						break;
					case 2:
						myGui.txtInfoPlayer3.setText(ui);
						break;
					case 3:
						myGui.txtInfoPlayer4.setText(ui);
						break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		myAddBehaviour(new startTurnBehaviour());
	}
	
	public void stopGame() {
		removeBehaviour(currentBehaviour);
		currentBehaviour = null;
		
		// remove the players
		try {
			for(int i=0; i<maxPlayers; i++)
				acPlayers[i].kill();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (acPlayers != null) { 
				for(int i=0; i<maxPlayers; i++) {
					acPlayers[i] = null;
				}
				acPlayers = null;
			}
		}
		
		// UI updates
		// switch enabled action buttons
		myGui.btnStopGame.setEnabled(false);
		myGui.comboPlayer1.setEnabled(true);
		myGui.comboPlayer3.setEnabled(true);
		myGui.comboPlayer2.setEnabled(true);
		myGui.comboPlayer4.setEnabled(true);
		myGui.btnStartGame.setEnabled(true);
	}
	
	@SuppressWarnings("serial")
	class startTurnBehaviour extends OneShotBehaviour {
		public void action() {
			try {
				String s = "A new round has started, Player" + (currentPlayer+1) + " is playing.";
				consoleLog(s);
				System.out.println(s);
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
				msg.setContent("Somebody set up us the bomb.");
				send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			myAddBehaviour(new doTurnBehaviour());
		}
	}
	class doTurnBehaviour extends OneShotBehaviour {
		public void action() {
			try {
				ACLMessage msg = myAgent.blockingReceive();
				int performative = msg.getPerformative();
				String content = msg.getContent();
				
				switch(performative) {
					case ACLMessage.INFORM:
						int nextPlayer = getNextPlayer();
						// notify every player of turn switch
						for(int i=0; i<maxPlayers; i++) {
							if(!isPlaying[i])
								continue;

							debugLog("Notifying player " + i+1 + "of new turn.");
							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));
							if(nextPlayer == i)
								msg2.setContent("Main screen turn on.");
							else
								msg2.setContent("You have no chance to survive make your time.");
							send(msg2);
						}
						
						// end turn
						if(content.equals("Treasure what little time remains of your lives.")) {
							consoleLog(msg.getSender() + "passed turn.");
						} else if(content.equals("all your base are belong to us")) {
							if(levels[currentPlayer] == LEVEL3) {
								credits[currentPlayer] += FINAL_SCORE;
								isPlaying[currentPlayer] = false;
								consoleLog(msg.getSender().getName() + " has completed all levels.");
								
								if(!activePlayersAvailable())
									declareWinner();
							} else {
								credits[currentPlayer] += (levels[currentPlayer] == LEVEL1) ? LEVEL2_SCORE : LEVEL3_SCORE;
								levels[currentPlayer] = (levels[currentPlayer] == LEVEL1) ? LEVEL2 : LEVEL3;
								consoleLog(msg.getSender().getName()
											+ " has leveled up and is now on level "
											+ levels[currentPlayer] + ".");
								
								String ui = "Player "+ (currentPlayer+1) + " ["+ credits[currentPlayer] +"] " + goalWords[currentPlayer];
								switch (currentPlayer) {
									case 0: myGui.txtInfoPlayer1.setText(ui); break;
									case 1: myGui.txtInfoPlayer2.setText(ui); break;
									case 2: myGui.txtInfoPlayer3.setText(ui); break;
									case 3: myGui.txtInfoPlayer4.setText(ui); break;
									default: break;
								}
							}
						} else if(content.equals("I'll be outside playing.")) {
							isPlaying[currentPlayer] = false;
						}
						
						currentPlayer = nextPlayer;
						if(activePlayersAvailable())
							myAddBehaviour(new startTurnBehaviour());
						else
							stopGame();
						break;
					// REQUEST FOR PROPOSAL
					case ACLMessage.REQUEST:
							// save the letter
							requestLetter = content;
							// handle proposals
							collectProposalsBehaviour cp = new collectProposalsBehaviour();
							addBehaviour(cp);
						break;
					default: // invalid message, go for it again!
						myAddBehaviour(new doTurnBehaviour());
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	class collectProposalsBehaviour extends OneShotBehaviour {
		private static final long serialVersionUID = 4921474044670600062L;
		public void action() {		
			try {
				// ask for proposals
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				request.setContent(requestLetter);
		
				for(int i = 0; i < acPlayers.length; i++) {
					if(i != currentPlayer)
						request.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));			
				}
				send(request);
		
				// get proposals
				int answers=0;
				String proposals="";
				while(answers < 3) {
					ACLMessage proposalMessage = new ACLMessage(ACLMessage.PROPOSE);
					proposalMessage = blockingReceive();
					String proposal = proposalMessage.getContent();
					// he wants to sell
					if (!proposal.equals("Video is being routed to the main screen.")) {
						proposals += proposalMessage.getSender().getName() + "#" + proposal + ";";
					}
					answers++;
				}
		
				// send list of proposals to the current player
				ACLMessage retrieve = new ACLMessage(ACLMessage.PROPOSE);
				retrieve.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
				retrieve.setContent(proposals);
				send(retrieve);
				
				addBehaviour(new waitDecisionBehaviour());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	class waitDecisionBehaviour extends OneShotBehaviour {
		public void action() {
			try {
				// get player's decision
				ACLMessage decisionMessage = myAgent.blockingReceive();
				String proposal = decisionMessage.getContent();
				int performative = decisionMessage.getPerformative();
				
				switch(performative) {
					case ACLMessage.ACCEPT_PROPOSAL:
						ACLMessage updateMessage = new ACLMessage(ACLMessage.INFORM);
						
						// update status
						String[] details = proposal.split(";");
						credits[currentPlayer] -= Integer.parseInt(details[1]);
						credits[Integer.parseInt(""+details[0].charAt(6))-1] += Integer.parseInt(details[1]);
						// buyer
						updateMessage.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
						updateMessage.setContent("DELETE;"+details[1]+";"+details[2]);
						send(updateMessage);
						String ui = "Player "+ (currentPlayer+1) + " ["+ credits[currentPlayer] +"] " + goalWords[currentPlayer];
						switch (currentPlayer) {
							case 0: myGui.txtInfoPlayer1.setText(ui); break;
							case 1: myGui.txtInfoPlayer2.setText(ui); break;
							case 2: myGui.txtInfoPlayer3.setText(ui); break;
							case 3: myGui.txtInfoPlayer4.setText(ui); break;
							default: break;
						}
						// seller
						updateMessage.addReceiver(new AID(details[0], AID.ISLOCALNAME));
						updateMessage.setContent("UPDATE;"+details[1]+";"+details[2]);
						send(updateMessage);
						System.out.println("Accepted proposal: "+proposal);
						int player = Integer.parseInt(""+details[0].charAt(6))-1;
						String ui2 = "Player "+ (player+1) + " ["+ credits[player] +"] " + goalWords[player];
						switch (player) {
							case 0: myGui.txtInfoPlayer1.setText(ui2); break;
							case 1: myGui.txtInfoPlayer2.setText(ui2); break;
							case 2: myGui.txtInfoPlayer3.setText(ui2); break;
							case 3: myGui.txtInfoPlayer4.setText(ui2); break;
							default: break;
						}
						break;
					case ACLMessage.REJECT_PROPOSAL:
						addBehaviour(new doTurnBehaviour());
						break;
					default:
						break;
				}
				myAddBehaviour(new doTurnBehaviour());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void consoleLog(String message) {
		myGui.consoleLog("[GAME] " + message);
	}
	
	private void debugLog(String message) {
		myGui.debugLog(message);
	}
	
	private void myAddBehaviour(OneShotBehaviour behaviour) {
		currentBehaviour = behaviour;
		addBehaviour(behaviour);
	}
	
	private int getNextPlayer() {
		int offsetMaxPlayers = maxPlayers-1;
		int nextPlayer = (currentPlayer == offsetMaxPlayers) ? 0 : currentPlayer+1;
		if(!isPlaying[nextPlayer]) {
			int counter = 1;
			do {
				counter++;
				if(nextPlayer == offsetMaxPlayers)
					nextPlayer = 0;
				else
					nextPlayer++;
			} while (counter < offsetMaxPlayers && !isPlaying[nextPlayer]);
		}
		
		return nextPlayer;
	}	
	
	private boolean activePlayersAvailable() {
		int counter = 0;
		for(int i = 0; i < maxPlayers; i++) {
			if (isPlaying[i])
				counter++;
		}
		return (counter >= 2);
	}
	
	private void declareWinner() {
		int winner = 0;
		
		for(int i = 0; i < maxPlayers; i++) {
			if (credits[i] > credits[winner])
				winner = i;
		}
		
		consoleLog("== Player" + (winner+1) + " has won the game with " + credits[winner] + " credits! ==");
	}
	
	private String[] old_getStartingLetters(String[] words) {
		String[] sletters = new String[maxPlayers];
		String letters = new String();

		for(int i=0; i < words.length; i++)
			letters += words[i];

		for(int i=0; i < maxPlayers; i++)
			sletters[i] = letters;

		return sletters;
	}
	private String[] getStartingLetters(String[] words) {
		String letters = new String();

		// concat all words into a string
		for(int i=0; i < words.length; i++)
			letters += words[i];

		// build a character array from that string
		// e.g every letter is an item of this array
		List<Character> characters = new ArrayList<Character>();
        for(char c:letters.toCharArray())
            characters.add(c);

        // randomly order the characters
        StringBuilder output = new StringBuilder(letters.length());
        while(characters.size()!=0){
            int randPicker = (int)(Math.random()*characters.size());
            output.append(characters.remove(randPicker));
        }

        // split the random characters array by players
        // total characters = maxPlayers x (4-letter words + 5-letter words + 6-letter words)
		String[] sletters = output.toString().split("(?<=\\G.{15})");

		debugLog("== AgentGameController::getStartingLetters() ==");
		debugLog(Arrays.toString(sletters));

		return sletters;
	}

	private String[] getGoalWords() {
		
		String[] gwords = new String[maxPlayers*3];
		int[] rnd = new int[maxPlayers];
		Random gen = new Random();

		try {
			FileReader input = new FileReader("words/word4.txt");
			BufferedReader bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < maxPlayers ; i++)
				rnd[i] = gen.nextInt(370)+1;
			
			// ler palavras (tamanho 4)
			Arrays.sort(rnd);
			for(int i=0, adj=0; i < maxPlayers; adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 5);
				String word = bufRead.readLine();
				gwords[i] = word;
			}
			input.close();
			
			input = new FileReader("words/word5.txt");
			bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < maxPlayers ; i++)
				rnd[i]=gen.nextInt(370)+1;
			
			// ler palavras (tamanho 5)
			Arrays.sort(rnd);
			for(int i=0,adj=0; i < maxPlayers ; adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 6);
				String word = bufRead.readLine();
				gwords[maxPlayers + i] = word;
			}
			input.close();
			
			input = new FileReader("words/word6.txt");
			bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < maxPlayers ; i++)
				rnd[i]=gen.nextInt(370)+1;

			// ler palavras (tamanho 6)
			Arrays.sort(rnd);
			for(int i=0,adj=0; i < maxPlayers; adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 7);
				String word = bufRead.readLine();
				gwords[maxPlayers*2 + i] = word;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		debugLog("== AgentGameController::getGoalWords() ==");
		debugLog(Arrays.toString(gwords));

		return gwords;
	}
}
