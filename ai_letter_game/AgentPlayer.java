package ai_letter_game;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentPlayer extends MockAgent 
{
	public AgentPlayer() {
		this.serviceDescriptionType = "player" + hashCode();
		this.serviceDescriptionName = "game-player" + hashCode();
	}

	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new CyclicBehaviour(this) 
        {
             public void action() 
             {
                ACLMessage msg= receive();
                if (msg!=null)
                    System.out.println( " - " +
                       myAgent.getLocalName() + " <- " +
                       msg.getContent() );
                block();
             }
        });
	}
}
