package ai_letter_game;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;

@SuppressWarnings("serial")
public class AgentGameController extends MockAgent {
	private LetterGameGui myGui = null;
	private AgentContainer ac = null;

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
		ac = getContainerController();
	}

	public void startGame() {
		// setup listener to other agents messages
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				ACLMessage msg = receive();
				if (msg != null)
					System.out.println("== Answer" + " <- " + msg.getContent()
							+ " from " + msg.getSender().getName());
				block();
			}
		});

		// create the players
		try {
			System.out.println(ac == null);
			(ac.createNewAgent("Player1", "ai_letter_game.AgentPlayer", null))
					.start();
			(ac.createNewAgent("Player2", "ai_letter_game.AgentPlayer", null))
					.start();
			(ac.createNewAgent("Player3", "ai_letter_game.AgentPlayer", null))
					.start();
			(ac.createNewAgent("Player4", "ai_letter_game.AgentPlayer", null))
					.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			myGui = null;
			ac = null;
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

	}
}
