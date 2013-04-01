package ai_letter_game;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class AgentPlayer extends MockAgent 
{
	// Logging
	private static final int LOG_INFO = 0;
	private static final int LOG_DEBUG = 1;
	private static int logLevel = LOG_DEBUG;

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

	// ### HELPERS
	public int getLevel() {
		return level;
	}

	public String getLetters() {
		return letters;
	}

	public String currentGoalWord() {
		return goalStrings[getLevel()];
	}

	/**
	 * Checks if the agent needs this letter or can sell it
	 * @param received
	 * @return
	 */
	protected boolean isSellable(char letter) {
		int timesNeeded = timesInWord(letter, currentGoalWord());
		int timesPresent = timesInWord(letter, getLetters());

		return (timesNeeded > 0) ? (timesPresent > timesNeeded) : true;
	}

	/**
	 * @return how many times the letter is present in word
	 */
	private int timesInWord(char letter, String word) {
		String tmp = new String(word);
		int timesPresent = 0;
		int index = tmp.indexOf(letter);
		while ( index != -1 ) {
			timesPresent++;
			tmp = tmp.substring(0,index) + tmp.substring(index+1);

			index = tmp.indexOf(letter);
		}

		return timesPresent;
	}

	private void debugLog(String message) {
		if(logLevel == LOG_DEBUG)
			System.out.println("[PL. " + this.getLocalName() + " DEBUG] " + message);
	}

	// ### LOGIC
	protected boolean canBuildLevelGoalString() {
		String characterList = new String(getLetters());
		String goalWord = new String(currentGoalWord());

		for(int i = 0; i < goalWord.length(); i++) {
			char characterAtPosition = goalWord.charAt(i);
			int index = characterList.indexOf(characterAtPosition);

			if(index == -1) {
				// characters missing
				return false;
			} else {
				// remove from character list
				characterList = characterList.substring(0,index)+characterList.substring(index+1);
			}
		}

		return true;
	}

	/**
	 * Returns the next missing letter to reach the goal word, if any.
	 * 
	 * @return the next missing letter
	 * @return the string "NO" if the agent already has all letters
	 */
	protected String neededLetter() {
		// return right away if there isn't a missing letter
		if (canBuildLevelGoalString())
			return new String("NO");

		return neededLetter(getLetters(), currentGoalWord());
	}

	private String neededLetter(String letters, String word) {
		String allLetters = new String(letters);
		String tmpWord = new String(word);

		// return the first unavailable letter
		char c = tmpWord.charAt(0);
		if(allLetters.indexOf(c) == -1) {
			return new String(""+c);
		} else {
			tmpWord = tmpWord.substring(1);
			allLetters = allLetters.substring(0,allLetters.indexOf(c))+allLetters.substring(allLetters.indexOf(c)+1);
			return neededLetter(allLetters, tmpWord);
		}
	}

	protected String selectProposal(String received) {
		debugLog("Selecting proposals from: " + received);

		String selected = new String("For great justice.");
		int min = -1;

		if (received.isEmpty())
			return selected;

		String[] proposals = received.split(";");
		for(String proposal : proposals) {
			String[] details = proposal.split("#");
			int price = Integer.parseInt(details[1]);
			if (credits >= price) {
				if(min == -1 || min > price) {
					min = price;
					selected = details[0].substring(0,7) + ";" + price + ";";
				}
			}
		}
		return selected;
	}

	// ### AGENT BEHAVIOUR
	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new initGamestateBehaviour());
	}

	class initGamestateBehaviour extends OneShotBehaviour {
		public void action() {
			debugLog("### initGamestateBehaviour");

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
			debugLog("### waitTurnBehaviour");
			debugLog("has " + letters + " for " + goalStrings[level]);

			// get INFORM to start our turn
			ACLMessage startTurn = new ACLMessage(ACLMessage.INFORM);
			startTurn = myAgent.blockingReceive();

			// do we need a letter?
			String reqLetter = ((AgentPlayer) myAgent).neededLetter();
			if(reqLetter.equals("NO")) {
				debugLog("### --- no letter needed");
			} else if(reqLetter.length() > 0) {
				debugLog("### --- needs " + reqLetter );

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
//				System.out.println("Received proposals: " + proposals);
				String decision = ((AgentPlayer) myAgent).selectProposal(proposals); 
				ACLMessage decisionMessage;
				if (!decision.equals("For great justice.")) {
					decisionMessage= new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					decision += reqLetter;
				} else {
					decisionMessage = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
					totalFails++;
				}
				decisionMessage.setContent(decision);
				decisionMessage.addReceiver(new AID("GameController", AID.ISLOCALNAME));
				send(decisionMessage);
				
				addBehaviour(new waitCFPBehaviour());
				return;
			}

			// notify the game controller turn has ended. 
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID("gameController", AID.ISLOCALNAME));
			if(canBuildLevelGoalString()) {	// have we completed this level?
				msg.setContent("all your base are belong to us");
				level = (level == 0) ? 1 : 2;
				lastRequested = 0;
				totalFails = 0;
			} else if(totalFails == 10) { // this is the end, my friend.
				msg.setContent("I'll be outside playing.");
			} else { // to continue searching
				msg.setContent("Treasure what little time remains of your lives.");
			}
			send(msg);

//			debugLog(msg.toString());

			// wait for CFP
			addBehaviour(new waitCFPBehaviour());
		}
	}


	class waitCFPBehaviour extends OneShotBehaviour {
		public void action() {
			debugLog("### waitCFPBehaviour");
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

				debugLog("### --- performative: " + ACLMessage.getPerformative(performative));
				switch(performative) {
					case ACLMessage.INFORM:
						if(content.equals("Main screen turn on.")) { // is it our turn?
//							System.out.println(myAgent.getName() + " next up - my turn"); 

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
//							System.out.println(myAgent.getName() + " next up - CFP"); 

							addBehaviour(new waitCFPBehaviour());
						}
						break;
					case ACLMessage.REQUEST:
						debugLog("### --- requested letter: " + content.charAt(0));

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
						if(letters.indexOf(c) == -1) { // no such letter here
							debugLog("### --- no such letter");

							message.setContent("Video is being routed to the main screen.");
						} else if(!isSellable(c)) { // needs letter
							debugLog("### --- this letter is needed to build goal word");

							message.setContent("Video is being routed to the main screen.");
						} else {
							debugLog("### --- making a proposal");

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
