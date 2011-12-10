package ai_letter_game;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

@SuppressWarnings("serial")
public class AgentGameController extends MockAgent {
	private LetterGameGui myGui = null;
	private AgentContainer container = null;
	private AgentController acPlayer1 = null;
	private AgentController acPlayer2 = null;
	private AgentController acPlayer3 = null;
	private AgentController acPlayer4 = null;

	public AgentGameController() {
		this.serviceDescriptionType = "controller" + hashCode();
		this.serviceDescriptionName = "game-controller" + hashCode();

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
		// setup listener to other agents messages
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null)
					consoleLog("== Answer" + " <- " + msg.getContent() + " from " + msg.getSender().getName());
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// arranging little delay, until the player agents are loaded
		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// wakie wakie!
		AMSAgentDescription[] agents = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(-1));
			agents = AMSService.search(this, new AMSAgentDescription(), c);
		} catch (Exception e) {
			System.out.println("Problem searching AMS: " + e);
			e.printStackTrace();
		}

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("Ping");

		for (int i = 0; i < agents.length; i++)
			msg.addReceiver(agents[i].getName());

		send(msg);

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
