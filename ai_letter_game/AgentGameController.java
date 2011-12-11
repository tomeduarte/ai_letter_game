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
	private int currentPlayer = 0;
	
	// game state
	private final int startMoney = 5;
	private int levels[] = { LEVEL1, LEVEL1, LEVEL1, LEVEL1 }; 
	
	public AgentGameController() {
		this.serviceDescriptionType = "controller" + hashCode();
		this.serviceDescriptionName = "game-controller" + hashCode();

		// Set up the gui
		myGui = new LetterGameGui(this);
		myGui.setVisible(true);
		
		// Initialize the state machine
	}

	@Override
	protected void setup() {
		super.setup();

		// Get the Container
		container = getContainerController();
	}

	public void startGame() {
		// setup listener to other agents messages
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null)
					consoleLog("got message '" + msg.getContent() + "', from " + msg.getSender().getName());
				block();
			}
		});

		// create the players
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
		// wakie wakie!
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

		addBehaviour(new startTurnBehaviour());
	}

	public void stopGame() {
		// remove the players
		try {
			for(int i=0; i<4; i++)
				acPlayers[i].kill();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			for(int i=0; i<4; i++)
				acPlayers[i] = null;
			acPlayers = null;
		}
	}
	
	@SuppressWarnings("serial")
	class startTurnBehaviour extends OneShotBehaviour {

		public void action() {
			try {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID(acPlayers[currentPlayer].getName(), AID.ISGUID));
				msg.setContent("Somebody set up us the bomb.");
				send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void consoleLog(String message) {
		myGui.consoleLog("[GAME] " + message);
	}
	
}
