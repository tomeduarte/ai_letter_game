package ai_letter_game;

import javax.swing.text.html.HTMLDocument.Iterator;

import ai_letter_game.AgentGameController.doTurnBehaviour;

import jade.core.AID;
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
		addBehaviour(new initGamestateBehaviour());
	}
	
	protected boolean canBuildGoalString() {
		String tmp = new String(letters);
		
		for (int i = 0; i < goalString.length(); i++){
		    char c = goalString.charAt(i);        
		    int index = tmp.indexOf(c);
		    
		    if(index == -1)
		    	return false;
		    else
		    	tmp = tmp.substring(0,index)+tmp.substring(index+1);
		}
		
		return true;
	}
	
	class initGamestateBehaviour extends OneShotBehaviour {
		public void action() {
			// get the start message from the controller
			ACLMessage gameinfo = new ACLMessage(ACLMessage.INFORM);
			gameinfo = myAgent.blockingReceive();
			String[] info = gameinfo.getContent().trim().split(";");

			// set game state according to instructions from the controller
			credits = Integer.parseInt(info[0]);
			goalString = info[1];
			letters = info[2].toCharArray();
			
			// is it my turn next or should I wait for CFP ?
			if(Boolean.parseBoolean(info[3])) {
				addBehaviour(new waitTurnBehaviour());
			} else {
				addBehaviour(new waitCFPBehaviour());
			}
		}
	}
	class waitTurnBehaviour extends OneShotBehaviour {
		public void action() {
			// get INFORM to start our turn
			ACLMessage startTurn = new ACLMessage(ACLMessage.INFORM);
			startTurn = myAgent.blockingReceive();
			
			
			// LOOP
			// select actions according to my behaviour
			
			// LOOP END
			

			// notify the game controller turn has ended. 
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID("gameController", AID.ISLOCALNAME));
			if(canBuildGoalString()) {	// have we completed this level?
				msg.setContent("all your base are belong to us");
			} else {
				msg.setContent("Treasure what little time remains of your lives.");
			}
			send(msg);

			// wait for CFP
			addBehaviour(new waitCFPBehaviour());
		}
	}
	class waitCFPBehaviour extends OneShotBehaviour {
		public void action() {
			// get INFORM or CFP
			// 		get CFP: request for proposal on a given letter
			// 			act upon it according to our behaviour
			//				either try to sell or not; in the latter, doWait() needs to be on the sell behaviour to block this one.
			//			addBehaviour(new waitCFPBehaviour());
			
			//		get INFORM: turn ended
			//			start waitTurn or waitCFP
			try {
				ACLMessage msg = myAgent.blockingReceive();
				int performative = msg.getPerformative();
				String content = msg.getContent();
				
				switch(performative) {
					case ACLMessage.INFORM:
						// is it our turn?
						if(content.equals("Main screen turn on.")) {
							System.out.println(myAgent.getName() + " next up - my turn"); 
							addBehaviour(new waitTurnBehaviour());
						} else {
							System.out.println(myAgent.getName() + " next up - CFP"); 
							addBehaviour(new waitCFPBehaviour());
						}
						break;
					default:
						addBehaviour(new waitCFPBehaviour());
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}		}
	}
}