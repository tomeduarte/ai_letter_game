package ai_letter_game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
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
	
	// game state
	private final int startMoney = 10;
	protected OneShotBehaviour currentBehaviour;
	private String requestLetter;
	private List<String> playerIds;
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

		// Initialize the state machine
		this.currentPlayer = 0;
		this.playerIds = new ArrayList<String>();

		// Set up the gui
		myGui = new LetterGameGui(this);
		myGui.setVisible(true);
	}

	@Override
	protected void setup() {
		super.setup();

		// Get the Container
		container = getContainerController();
	}

	public void startGame() {
		debugLog("### .startGame()");
		currentBehaviour = null;

		// create the players
		try {
			// holders for agents and game state
			acPlayers 	= new AgentController[getMaxPlayers()];

			for(int i=0; i < getMaxPlayers(); i++) {
				// create AgentPlayer
				AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(i));
				acPlayers[i] = container.createNewAgent(agentInfo.getName(), "ai_letter_game.AgentPlayer", null);
				acPlayers[i].start();
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
			debugLog("### --- messages sent");
			for(int i=0; i<getMaxPlayers(); i++) {
				String[] words = { goalWords[i], goalWords[i+getMaxPlayers()], goalWords[i+getMaxPlayers()*2] };

				// send message to agent
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));
				// format: starting credits, one word per level (4, 5, 6 chars), starting letters, is it their turn?
				msg.setContent(startMoney + ";" + words[0] + ";" + words[1] + ";" + words[2] + ";" + startingLetters[i] + ";" + (currentPlayer == i));
				send(msg);

				debugLog(msg.toString());

				// update UI
				AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(i));
				agentInfo.setPoints(startMoney);
				agentInfo.setWords(words);
				agentInfo.setLetters(startingLetters[i]);
				agentInfo.setPlaying( true );
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
			for(int i=0; i< getMaxPlayers(); i++)
				acPlayers[i].kill();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (acPlayers != null) { 
				for(int i=0; i< getMaxPlayers(); i++) {
					acPlayers[i] = null;
				}
				acPlayers = null;
			}
		}
	}

	public AgentInformation addPlayer() {
		String[] emptyWords = { "----", "-----", "------" };
		AgentInformation agentInfo = new AgentInformation(getMaxPlayers());
		agentInfo.setName("Player");
		agentInfo.setWords(emptyWords);
		agentInfo.setLetters("-");
		agentInfo.setPoints(startMoney);
		agentInfo.setLevel(LEVEL1);
		agentInfo.setPlaying(true);

		playerIds.add(agentInfo.getPlayer_id());

		debugLog("Added a new player. Current count: " + getMaxPlayers());

		return agentInfo;
	}

	public void removePlayer(String playerId) {
		playerIds.remove(playerId);

		debugLog("Removed a player. Current count: " + getMaxPlayers());
	}

	class startTurnBehaviour extends OneShotBehaviour {
		public void action() {
			try {
				debugLog("### startTurnBehaviour");
				AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(currentPlayer));
				String s = "A new round has started, " + agentInfo.getName() + " is playing.";
				consoleLog(s);

				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
				msg.setContent("Somebody set up us the bomb.");
				send(msg);

				debugLog(msg.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			myAddBehaviour(new doTurnBehaviour());
		}
	}
	class doTurnBehaviour extends OneShotBehaviour {
		public void action() {
			try {
				debugLog("### doTurnBehaviour");
				ACLMessage msg = myAgent.blockingReceive();
				int performative = msg.getPerformative();
				String content = msg.getContent();

				debugLog("### --- msg");
				debugLog(msg.toString());

				switch(performative) {
					case ACLMessage.INFORM:
						int nextPlayer = getNextPlayer();
						// notify every player of turn switch
						for(int i=0; i<getMaxPlayers(); i++) {
							AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(i));
							if(!agentInfo.isPlaying())
								continue;

							ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
							msg2.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));
							if(nextPlayer == i)
								msg2.setContent("Main screen turn on.");
							else
								msg2.setContent("You have no chance to survive make your time.");
							send(msg2);

							debugLog("### --- INFORM: notifying " + agentInfo.getName() + " of new turn:");
							debugLog(msg2.toString());
						}

						// end turn
						AgentInformation currentPlayerInfo = myGui.getAgentInformation(playerIds.get(currentPlayer));
						if(content.equals("Treasure what little time remains of your lives.")) {
							consoleLog(msg.getSender() + "passed turn.");
						} else if(content.equals("all your base are belong to us")) {
							if(currentPlayerInfo.getLevel() == LEVEL3) {
								currentPlayerInfo.setPoints(currentPlayerInfo.getPoints() + FINAL_SCORE);
								currentPlayerInfo.setPlaying(false);
								consoleLog(currentPlayerInfo.getName() + " has completed all levels.");
								
								if(!activePlayersAvailable())
									declareWinner();
							} else {
								currentPlayerInfo.setPoints( (currentPlayerInfo.getLevel() == LEVEL1) ? LEVEL2_SCORE : LEVEL3_SCORE );
								currentPlayerInfo.setLevel( (currentPlayerInfo.getLevel() == LEVEL1) ? LEVEL2 : LEVEL3 );
								consoleLog(currentPlayerInfo.getName()
											+ " has leveled up and is now on level "
											+ currentPlayerInfo.getLevel() + ".");
							}
						} else if(content.equals("I'll be outside playing.")) {
							currentPlayerInfo.setPlaying(false);
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
				while(answers < (getMaxPlayers()-1)) {
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
						int sellerId = playerIds.indexOf("" + details[0].charAt(6));
						AgentInformation currentPlayerInfo = myGui.getAgentInformation(playerIds.get(currentPlayer));
						AgentInformation sellerPlayerInfo = myGui.getAgentInformation(playerIds.get(sellerId));

						currentPlayerInfo.setPoints( currentPlayerInfo.getPoints() - Integer.parseInt(details[1]) );
						currentPlayerInfo.setLetters(currentPlayerInfo.getLetters() + details[2]);
						sellerPlayerInfo.setPoints( sellerPlayerInfo.getPoints() + Integer.parseInt(details[1]) );
						sellerPlayerInfo.setLetters( sellerPlayerInfo.getLetters().replace(details[2], ""));
						
						// buyer
						updateMessage.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
						updateMessage.setContent("DELETE;"+details[1]+";"+details[2]);
						send(updateMessage);
						
						// seller
						updateMessage.addReceiver(new AID(details[0], AID.ISLOCALNAME));
						updateMessage.setContent("UPDATE;"+details[1]+";"+details[2]);
						send(updateMessage);
						System.out.println("Accepted proposal: "+proposal);
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
		if(myGui != null) {
			myGui.debugLog(message);
		}
	}
	
	private void myAddBehaviour(OneShotBehaviour behaviour) {
		currentBehaviour = behaviour;
		addBehaviour(behaviour);
	}
	
	/**
	 * @return the maxPlayers
	 */
	public int getMaxPlayers() {
		return playerIds.size();
	}

	private int getNextPlayer() {
		int offsetMaxPlayers = getMaxPlayers() - 1;
		int nextPlayer = (currentPlayer == offsetMaxPlayers) ? 0 : currentPlayer+1;
		AgentInformation nextPlayerInfo = myGui.getAgentInformation(playerIds.get(nextPlayer));

		if(!nextPlayerInfo.isPlaying()) {
			int counter = 1;
			do {
				counter++;
				if(nextPlayer == offsetMaxPlayers)
					nextPlayer = 0;
				else
					nextPlayer++;

				nextPlayerInfo = myGui.getAgentInformation(playerIds.get(nextPlayer));
			} while (counter < offsetMaxPlayers && !nextPlayerInfo.isPlaying());
		}

		return nextPlayer;
	}	

	private boolean activePlayersAvailable() {
		int counter = 0;
		for(int i = 0; i < playerIds.size(); i++) {
			AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(i));
			if (agentInfo.isPlaying())
				counter++;
		}
		return (counter >= 2);
	}

	private void declareWinner() {
		AgentInformation winner = myGui.getAgentInformation(playerIds.get(0));
		
		for(int i = 1; i < playerIds.size(); i++) {
			AgentInformation agentInfo = myGui.getAgentInformation(playerIds.get(i));
			if (agentInfo.getPoints() > winner.getPoints())
				winner = agentInfo;
		}
		
		consoleLog("== " + winner.getName() + " has won the game with " + winner.getPoints() + " credits! ==");
	}

	private String[] old_getStartingLetters(String[] words) {
		String[] sletters = new String[getMaxPlayers()];
		String letters = new String();

		for(int i=0; i < words.length; i++)
			letters += words[i];

		for(int i=0; i < getMaxPlayers(); i++)
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
		
		String[] gwords = new String[getMaxPlayers()*3];
		int[] rnd = new int[getMaxPlayers()];
		Random gen = new Random();

		try {
			FileReader input = new FileReader("words/word4.txt");
			BufferedReader bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < getMaxPlayers() ; i++)
				rnd[i] = gen.nextInt(370)+1;
			
			// ler palavras (tamanho 4)
			Arrays.sort(rnd);
			for(int i=0, adj=0; i < getMaxPlayers(); adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 5);
				String word = bufRead.readLine();
				gwords[i] = word;
			}
			input.close();
			
			input = new FileReader("words/word5.txt");
			bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < getMaxPlayers() ; i++)
				rnd[i]=gen.nextInt(370)+1;
			
			// ler palavras (tamanho 5)
			Arrays.sort(rnd);
			for(int i=0,adj=0; i < getMaxPlayers() ; adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 6);
				String word = bufRead.readLine();
				gwords[getMaxPlayers() + i] = word;
			}
			input.close();
			
			input = new FileReader("words/word6.txt");
			bufRead = new BufferedReader(input);
	
			// gerar indexes para linhas aleatorias
			for(int i=0 ; i < getMaxPlayers() ; i++)
				rnd[i]=gen.nextInt(370)+1;

			// ler palavras (tamanho 6)
			Arrays.sort(rnd);
			for(int i=0,adj=0; i < getMaxPlayers(); adj=rnd[i++]) {
				bufRead.skip((rnd[i]-adj) * 7);
				String word = bufRead.readLine();
				gwords[getMaxPlayers()*2 + i] = word;
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
