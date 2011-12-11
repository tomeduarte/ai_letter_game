package ai_letter_game;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentPlayer extends MockAgent 
{
	private int credits;
	private String goalString;
	private char[] letters;
	
	public AgentPlayer() {
		this.serviceDescriptionType = "player" + hashCode();
		this.serviceDescriptionName = "game-player" + hashCode();
	}

	@Override
	protected void setup() {
		super.setup();
//		addBehaviour(new CyclicBehaviour(this) 
//        {
//             public void action() 
//             {
//                ACLMessage msg= receive();
//                if (msg!=null)
//                    System.out.println( " - " +
//                       myAgent.getLocalName() + " <- " +
//                       msg.getContent() );
//                block();
//             }
//        });
		addBehaviour(new initGamestateBehaviour());
	}
	
	@SuppressWarnings("serial")
	class initGamestateBehaviour extends OneShotBehaviour {

		public void action() {
			ACLMessage gameinfo = new ACLMessage(ACLMessage.INFORM);
			gameinfo = myAgent.blockingReceive();
			String[] info = gameinfo.getContent().trim().split(";");

			credits = Integer.parseInt(info[0]);
			goalString = info[1];
			letters = info[2].toCharArray();
			
			System.out.println(credits);
			System.out.println(goalString);
			System.out.println(letters);
//			getWordBehaviour gwb = new getWordBehaviour();
//			addBehaviour(gwb);
		}
	}
}
