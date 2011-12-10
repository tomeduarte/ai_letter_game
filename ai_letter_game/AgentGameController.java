package ai_letter_game;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentGameController extends MockAgent 
{
	public AgentGameController() {
		this.serviceDescriptionType = "controller" + hashCode();
		this.serviceDescriptionName = "game-controller" + hashCode();
	}

	@Override
	protected void setup() {
		super.setup();
		// arranging little delay, until the player agents are loaded
		try {
			Thread.sleep(2000l);
			AMSAgentDescription [] agents = null;
	      	try {
	            SearchConstraints c = new SearchConstraints();
	            c.setMaxResults (new Long(-1));
				agents = AMSService.search( this, new AMSAgentDescription (), c );
			}
			catch (Exception e) {
	            System.out.println( "Problem searching AMS: " + e );
	            e.printStackTrace();
			}
			
			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setContent( "Ping" );

			for (int i=0; i<agents.length;i++)
				msg.addReceiver( agents[i].getName() ); 

			send(msg);
			
			addBehaviour(new CyclicBehaviour(this) 
			{
				 public void action() {
					ACLMessage msg= receive();
					if (msg!=null)
						System.out.println( "== Answer" + " <- " 
						 +  msg.getContent() + " from "
						 +  msg.getSender().getName() );
					block();
				 }
			});
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
