package ai_letter_game;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
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
	private AgentController acPlayer1 = null;
	private AgentController acPlayer2 = null;
	private AgentController acPlayer3 = null;
	private AgentController acPlayer4 = null;

	// game levels
	private final int LEVEL1 = 1;
	private final int LEVEL2 = 2;
	private final int LEVEL3 = 3;
	
	// current player
	private AgentController acCurrentPlayer = null;
	
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
			acPlayer1 = container.createNewAgent("Player1", "ai_letter_game.AgentPlayer", null);
			acPlayer2 = container.createNewAgent("Player2", "ai_letter_game.AgentPlayer", null);
			acPlayer3 = container.createNewAgent("Player3", "ai_letter_game.AgentPlayer", null);
			acPlayer4 = container.createNewAgent("Player4", "ai_letter_game.AgentPlayer", null);
			acPlayer1.start();
			acPlayer2.start();
			acPlayer3.start();
			acPlayer4.start();
						
			acCurrentPlayer = acPlayer1;
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
//		AMSAgentDescription[] agents = null;
//		try {
//			SearchConstraints c = new SearchConstraints();
//			c.setMaxResults(new Long(-1));
//			agents = AMSService.search(this, new AMSAgentDescription(), c);
//		} catch (Exception e) {
//			System.out.println("Problem searching AMS: " + e);
//			e.printStackTrace();
//		}

		try {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(acPlayer1.getName(), AID.ISGUID));
			msg.setContent(startMoney+";hello;aiad");
			
			send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		for (int i = 0; i < agents.length; i++)
//			msg.addReceiver(agents[i].getName());

	}

	public void stopGame() {
		// remove the players
		try {
			acPlayer1.kill();
			acPlayer2.kill();
			acPlayer3.kill();
			acPlayer4.kill();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			acPlayer1 = null;
			acPlayer2 = null;
			acPlayer3 = null;
			acPlayer4 = null;
		}
	}
	
	private void consoleLog(String message) {
		myGui.consoleLog("[GAME] " + message);
	}
	
}
