package aiad;
import jade.Boot3;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AIAD 
{
	public AIAD() { }

	public void go()
	{
		Boot3 bootInstance = null;
		File projectDir = new File(".");
		List<String> argsList = new ArrayList<String>();
		try {
			argsList.add("-classpath");
			argsList.add(projectDir.getAbsolutePath() + "\\classes");
			argsList.add("Sniffer:jade.tools.sniffer.Sniffer gameController:AgentGameController");
			
			String[] arguments = new String[argsList.size()];
			arguments = argsList.toArray(arguments);
			bootInstance = new Boot3(arguments);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		} 
		finally {
			if (bootInstance != null) {
				bootInstance = null;
			}
		}
	}
	
	public static void main(String[] args) {
		(new AIAD()).go();
	}
}