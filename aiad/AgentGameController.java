package aiad;

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
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
