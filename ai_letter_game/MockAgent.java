package ai_letter_game;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

@SuppressWarnings("serial")
public abstract class MockAgent extends Agent 
{
	protected String serviceDescriptionType;
	protected String serviceDescriptionName;

	public MockAgent() { }

	public String getServiceDescriptionType() {
		return serviceDescriptionType;
	}

	public void setServiceDescriptionType(String sdType) {
		this.serviceDescriptionType = sdType;
	}

	public String getServiceDescriptionName() {
		return serviceDescriptionName;
	}

	public void setServiceDescriptionName(String sdName) {
		this.serviceDescriptionName = sdName;
	}

	@Override
	protected void setup() {
		registerService();
	}

	@Override
	protected void takeDown() {
		deregisterService();
	}

	protected void registerService() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(getServiceDescriptionType());
		sd.setName(getServiceDescriptionName());
		
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} 
		catch (FIPAException fe) {
			throw new IllegalStateException("Appeared problem during the service registration.", fe);
		}
	}
	
	protected void deregisterService() {
		try {
			DFService.deregister(this);
		} 
		catch (FIPAException fe) {
			throw new IllegalStateException("Appeared problem during the service deregistration.", fe);
		}
	}
}
