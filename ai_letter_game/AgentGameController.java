package ai_letter_game;

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
	
	// current player
	private int currentPlayer;
	
	// game state
	private final int startMoney = 5;
	protected OneShotBehaviour currentBehaviour;
	private int levels[] = { LEVEL1, LEVEL1, LEVEL1, LEVEL1 }; 
	
	public AgentGameController() {
		this.serviceDescriptionType = "controller" + hashCode();
		this.serviceDescriptionName = "game-controller" + hashCode();

		// Set up the gui
		myGui = new LetterGameGui(this);
		myGui.setVisible(true);
		
		// Initialize the state machine
		currentPlayer = 0;
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
		currentPlayer = 0;
		try {
			acPlayers = new AgentController[4];
			for(int i=0; i < 4; i++) {
				acPlayers[i] = container.createNewAgent("Player"+(i+1), "ai_letter_game.AgentPlayer", null);
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
		try {
			for(int i=0; i<4; i++) {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[i].getName(), AID.ISGUID));
				msg.setContent(startMoney+";hello;olleh;"+(currentPlayer == i));
				send(msg);
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
			for(int i=0; i<4; i++)
				acPlayers[i].kill();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (acPlayers != null) { 
				for(int i=0; i<4; i++) {
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
				String s = "A new round has started, Player" + currentPlayer + " is playing.";
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
						int nextPlayer = (currentPlayer == 3) ? 0 : currentPlayer+1;
						// notify every player of turn switch
						for(int i=0; i<4; i++) {
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
							currentPlayer = nextPlayer;
							myAddBehaviour(new startTurnBehaviour());
						} else if(content.equals("all your base are belong to us")) {
							if(levels[currentPlayer] == LEVEL3) {
								consoleLog(msg.getSender() + " has won the game.");
								stopGame();
								return;
							} else {
								levels[currentPlayer] = (levels[currentPlayer] == LEVEL1) ? LEVEL2 : LEVEL3;
								consoleLog(msg.getSender()
											+ " has leveled up and is now on level "
											+ levels[currentPlayer] + ".");
								currentPlayer = nextPlayer;
								myAddBehaviour(new startTurnBehaviour());
							}
						}
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
	
	private void consoleLog(String message) {
		myGui.consoleLog("[GAME] " + message);
	}
	
	private void myAddBehaviour(OneShotBehaviour behaviour) {
		currentBehaviour = behaviour;
		addBehaviour(behaviour);
	}
	
}
