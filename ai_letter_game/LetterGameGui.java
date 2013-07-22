package ai_letter_game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;


public class LetterGameGui extends JFrame implements ActionListener{

	private static final long serialVersionUID = -8092527406687437334L;

	// Logging
	private static final int LOG_INFO = 0;
	private static final int LOG_DEBUG = 1;
	private static int logLevel = LOG_DEBUG;

	// GameController Relay Object
	private AgentGameController myAgent;

	// UI objects
	MigLayout layout;
	JPanel globalPanel;
	JPanel playersPanel;
	JPanel controlsPanel;
	JScrollPane topScrollPane;
	JComboBox newPlayerBehaviourCombo;
	JButton newPlayerAddButton;
	JButton startGameButton;
	JButton stopGameButton;

	// agents information
	HashMap<String, AgentInformation> agentsInformation;

	/**
	 * Create the frame.
	 */
	public LetterGameGui(AgentGameController agent) {
		setVisible(false);
		
		// agent object which is a relay for game actions
		this.myAgent = agent;

		// setup the GUI elements
		setupGUILayout();

		// setup initial game state		
		setupDefaultGamestate();

		// update display
		showGUI();
	}

	/**
	 * Setup GUI elements and holder objects for dynamic content.
	 */
	private void setupGUILayout() {

		// ==== LAYOUT ====
		this.layout 	  	= new MigLayout("", "[grow, fill]", "[]");
		this.playersPanel 	= new JPanel(layout);
		this.controlsPanel  = new JPanel(layout);
		this.globalPanel 	= new JPanel(new MigLayout("debug", "[grow, fill]", "[]10[]"));

		// ==== TOP PANEL ====
		playersPanel.add(new JLabel(""), 			"sg delete");
		playersPanel.add(new JLabel("NOME"), 		"gap unrel, sg name");
		playersPanel.add(new JLabel("PALAVRA"), 	"gap unrel, sg palavra");
		playersPanel.add(new JLabel("LETRAS"), 		"gap unrel, sg letters");
		playersPanel.add(new JLabel("PONTOS"), 		"gap unrel, sg points");
		playersPanel.add(new JLabel("NÍVEL"), 		"gap unrel, sg level");
		playersPanel.add(new JLabel("A JOGAR?"), 	"gap unrel, sg playing, wrap");

		// ==== BOTTOM PANEL ====
		// number of players information
		JTextPane numberOfPlayers = new JTextPane();
		numberOfPlayers.setText("");
		JTextPane numberOfPlayersLegend = new JTextPane();
		numberOfPlayersLegend.setText("JOGADORES");

		// add new player form
		this.newPlayerBehaviourCombo = new JComboBox();
		this.newPlayerAddButton = new JButton("ADICIONAR");
		newPlayerAddButton.addActionListener(this);

		// start and stop buttons
		this.startGameButton = new JButton("Iniciar jogo");
		startGameButton.addActionListener(this);
		this.stopGameButton = new JButton("Parar jogo");
		stopGameButton.addActionListener(this);
		stopGameButton.setEnabled(false);

		// add elements to the bottom layout panel
//		controlsPanel.add(numberOfPlayers, "center, flowy, split 2");
//		controlsPanel.add(numberOfPlayersLegend, "center, width pref!");
//		controlsPanel.add(newPlayerBehaviourCombo, "center, flowy, split 2");
		controlsPanel.add(newPlayerAddButton, "center");
		controlsPanel.add(startGameButton, "gap unrel, growy");
		controlsPanel.add(stopGameButton, "growy");

		// append to global panel
        globalPanel.add(this.controlsPanel, "wrap");
        globalPanel.add(new JSeparator(), "wrap");
		this.topScrollPane = new JScrollPane(playersPanel);
		globalPanel.add(this.topScrollPane, "growy");
		getContentPane().add(this.globalPanel);
	}


	/**
	 * Update window settings to be visible 
	 */
	private void showGUI() {
		// window settings
		setTitle("A.I. Letter Game");
		setResizable(false);
		setAlwaysOnTop(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// display window
		pack();
		setBounds(200, 10, 1024, 768);
		setLocationRelativeTo(null);
	}

	/**
	 * Handle user actions
	 */
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		
		if(action.equals("Iniciar jogo")) {
			doLog("Starting game..");
			// update UI
			startGameButton.setEnabled(false);
			newPlayerBehaviourCombo.setEnabled(false);
			newPlayerAddButton.setEnabled(false);
			for (AgentInformation info : agentsInformation.values()) {
			    info.disableRemoveButton();
			}
			stopGameButton.setEnabled(true);

			// start agents
			myAgent.startGame();
			doLog("OK");
		} else if(action.equals("Parar jogo")) {
			doLog("Stopping game..");
			// stop agents
			myAgent.stopGame();

			// update UI
			stopGameButton.setEnabled(false);
			for (AgentInformation info : agentsInformation.values()) {
			    info.enableRemoveButton();
			}
			newPlayerBehaviourCombo.setEnabled(true);
			newPlayerAddButton.setEnabled(true);
			startGameButton.setEnabled(true);
			doLog("OK");
		} else if (action.equals("ADICIONAR")) {
			addNewPlayer();
		} else if (action.equals("remover")) {
			JButton clickedButton = (JButton) ae.getSource();
			removePlayer(clickedButton.getName());
		}
	}
	
	/**
	 *  Log messages to the Console Log
	 */
	protected void consoleLog(String message) {
		//txtConsoleLog.append("\n" + message);
		System.out.println(message);
	}

	/**
	 * Log debug messages to the console, if logLevel is set to LOG_DEBUG
	 */
	protected void debugLog(String message) {
		if(logLevel == LOG_DEBUG)
			consoleLog("[DEBUG] " + message);
	}

	/**
	 * Log messages from the UI
	 */
	private void doLog(String message) {
		consoleLog("[UI] " + message);
	}
	private void doLog(String message, boolean debug) {
		if(debug == true)
			consoleLog("[UI DEBUG] " + message);
	}

	/**
	 * Setup initial gamestate (two players, no more information)
	 */
	private void setupDefaultGamestate() {
		agentsInformation = new HashMap<String, AgentInformation>();
		addNewPlayer();
		addNewPlayer();
	}
	
	/**
	 * Adds a new player agent and updates the GUI
	 * 
	 * Essentially:
	 * 	1) add the new player to the game (through game controller relay)
	 * 	2) add player's information to the GUI
	 * 	3) repaint the relevant window part
	 */
	private void addNewPlayer() {
		AgentInformation info = myAgent.addPlayer();

		agentsInformation.put(info.getPlayer_id(), info);
		info.drawIn(playersPanel, this);

		doLog("Added a new player", true);
	}

	/**
	 * Remove player from the game and update the GUI
	 */
	private void removePlayer(String player_id) {
		myAgent.removePlayer(player_id);

		AgentInformation removedAgent = agentsInformation.remove(player_id);
		removedAgent.removeDrawnInformation();

		doLog("Removed player " + player_id, true);
	}
	
	/**
	 * Returns the game information for a given player id
	 */
	public AgentInformation getAgentInformation(String playerId) {
		return agentsInformation.get(playerId);
	}
}
