package ai_letter_game;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentPlayer extends MockAgent 
{
	private int credits;
	private int level;
	private int lastRequested;
	private int totalFails;
	private String[] goalStrings;
	private String letters;
	
	public AgentPlayer() {
		this.serviceDescriptionType = "player" + hashCode();
		this.serviceDescriptionName = "game-player" + hashCode();
		
		goalStrings = new String[3];
		letters = new String();
		lastRequested = 0;
		totalFails = 0;
	}

	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new initGamestateBehaviour());
	}
	
	protected boolean canBuildGoalString() {
		/*
		for(int j = 0; j < goalStrings.length; j++ ) {
			for (int i = 0; i < goalStrings[j].length(); i++) {
			    char c = goalStrings[j].charAt(i);        
			    int index = tmp.indexOf(c);
			    
			    if(index == -1)
			    	return false;
			    else
			    	tmp = tmp.substring(0,index)+tmp.substring(index+1);
			}
		}
		*/

		String tmp = new String(letters);
		String goalWord = goalStrings[level];

		for(int i = 0; i < goalWord.length(); i++) {
			char characterAtPosition = goalWord.charAt(i);
			int index = tmp.indexOf(characterAtPosition);

			if(index == -1) {
				// characters missing
				return false;
			} else {
				// remove from character list
				tmp = tmp.substring(0,index)+tmp.substring(index+1);
			}
		}

		return true;
	}
	
	protected String needLetter() {
		if (canBuildGoalString())
			return new String("NO MORE");

		char c;
		String word = goalStrings[level];
		String allLetters = new String(letters);

		if(lastRequested == word.length()) {
			lastRequested = 8;
			return new String("NO MORE");
		} else if (lastRequested == 8) {
			lastRequested = 0;
		}

		for(int i= lastRequested; i < word.length(); i++) {
			c = word.charAt(i);
			if(allLetters.indexOf(c) == -1) {
				lastRequested = i;
				return new String(""+c);
			}
		}
		return new String();
	}

	protected String selectProposal(String received) {
		int min = -1;
		String selected = new String("For great justice.");
		String[] proposals = received.split(";");
		for(String proposal : proposals) {
			String[] details = proposal.split("#");
			int price = Integer.parseInt(details[1]);
			if (credits > price) {
				if(min == -1 || min > price) {
					min = price;
					selected = details[0].substring(0,7) + ";" + price + ";";
					continue;
				}
			} else {
				return "For great justice.";
			}
		}
		return selected;
	}
	
	class initGamestateBehaviour extends OneShotBehaviour {
		public void action() {
			// get the start message from the controller
			ACLMessage gameinfo = new ACLMessage(ACLMessage.INFORM);
			gameinfo = myAgent.blockingReceive();
			String[] info = gameinfo.getContent().trim().split(";");

			// set game state according to instructions from the controller
			level = 0;
			credits = Integer.parseInt(info[0]);
			goalStrings[0] = info[1];
			goalStrings[1] = info[2];
			goalStrings[2] = info[3];
			letters = info[4];

			// is it my turn next or should I wait for CFP ?
			if(Boolean.parseBoolean(info[5])) {
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

			// do we need a letter?
			String reqLetter = ((AgentPlayer) myAgent).needLetter();
			if(reqLetter.equals("NO MORE")) {
				
			} else if(reqLetter.length() > 0) {
				// send request
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				request.addReceiver(new AID("GameController", AID.ISLOCALNAME));
				request.setContent(reqLetter);
				send(request);

				// wait for proposals
				ACLMessage answer = new ACLMessage(ACLMessage.PROPOSE);
				answer = myAgent.blockingReceive();
				String proposals = answer.getContent();

				// send decision
				System.out.println("Received proposals: " + proposals);
				String decision = ((AgentPlayer) myAgent).selectProposal(proposals); 
				ACLMessage decisionMessage;
				if (!decision.equals("For great justice.")) {
					decisionMessage= new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					decision += reqLetter;
				} else {
					decisionMessage = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				}
				decisionMessage.setContent(decision);
				decisionMessage.addReceiver(new AID("GameController", AID.ISLOCALNAME));
				send(decisionMessage);
				
				addBehaviour(new waitCFPBehaviour());
				return;
			}

			totalFails++;
			
			// notify the game controller turn has ended. 
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID("gameController", AID.ISLOCALNAME));
			if(canBuildGoalString()) {	// have we completed this level?
				msg.setContent("all your base are belong to us");
			} else if(totalFails == 10) {
				msg.setContent("I'll be outside playing.");
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
						if(content.equals("Main screen turn on.")) { // is it our turn?
							System.out.println(myAgent.getName() + " next up - my turn"); 

							addBehaviour(new waitTurnBehaviour());
						} else if (content.substring(0,7).equals("UPDATE;")) { // sold letter
							credits += Integer.parseInt(content.split(";")[1]);

							int index = letters.indexOf(content.split(";")[2].charAt(0));
							letters = letters.substring(0, index) + letters.substring(index+1);
							addBehaviour(new waitCFPBehaviour());
						} else if (content.substring(0,7).equals("DELETE;")) { // bought letter
							credits -= Integer.parseInt(content.split(";")[1]);
							letters = letters + content.split(";")[2];

							addBehaviour(new waitTurnBehaviour());
						} else {
							System.out.println(myAgent.getName() + " next up - CFP"); 

							addBehaviour(new waitCFPBehaviour());
						}
						break;
					case ACLMessage.REQUEST:
						ACLMessage message = new ACLMessage(ACLMessage.PROPOSE);
						message.addReceiver(new AID("gameController", AID.ISLOCALNAME));
						/**
						 * PRICES:
						 * ETAO 6
						 * INSHRDL 4
						 * CUMWFG 2
						 * YPBVKJXQZ 1
						 */
						String Six = "etao";
						String Four = "inshrdl";
						String Two = "cumwfg";
						String One = "ypbvkjxqz";
						char c = content.charAt(0);
						if(letters.indexOf(c) == -1) // no such letter here
							message.setContent("Video is being routed to the main screen.");
						else if(goalStrings[level].indexOf(c) != -1) // needs letter
							message.setContent("Video is being routed to the main screen.");
						else {
							if(Six.indexOf(c) != -1)
								message.setContent("6");
							else if(Four.indexOf(c) != -1)
								message.setContent("4");
							else if(Two.indexOf(c) != -1)
								message.setContent("2");
							else if(One.indexOf(c) != -1)
								message.setContent("1");
							else
								message.setContent("1");
						}
						send(message);
						addBehaviour(new waitCFPBehaviour());
						break;
					default:
						addBehaviour(new waitCFPBehaviour());
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
