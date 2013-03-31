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
		this.layout 	  	= new MigLayout("debug", "[grow, fill]", "[]");
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
		JComboBox newPlayerBehaviourCombo = new JComboBox();
		JButton newPlayerAddButton = new JButton("ADICIONAR");
		newPlayerAddButton.addActionListener(this);

		// start and stop buttons
		JButton startGameButton = new JButton("Start game!");
		startGameButton.addActionListener(this);
		JButton stopGameButton = new JButton("Stop game");
		stopGameButton.addActionListener(this);
		stopGameButton.setEnabled(false);

		// add elements to the bottom layout panel
		controlsPanel.add(numberOfPlayers, "center, flowy, split 2");
		controlsPanel.add(numberOfPlayersLegend, "center, width pref!");
		controlsPanel.add(newPlayerBehaviourCombo, "center, flowy, split 2");
		controlsPanel.add(newPlayerAddButton, "center");
		controlsPanel.add(startGameButton, "gap unrel, growy");
		controlsPanel.add(stopGameButton, "growy");

		// append to global panel
        globalPanel.add(this.controlsPanel, "wrap");
        globalPanel.add(new JSeparator(), "wrap");
		this.topScrollPane = new JScrollPane(playersPanel);
		globalPanel.add(this.topScrollPane, "growy");
		getContentPane().add(this.globalPanel);
		
		/* OLD GUI CODE
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		consoleLogScroll = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, consoleLogScroll, -250, SpringLayout.SOUTH, contentPane);
		consoleLogScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sl_contentPane.putConstraint(SpringLayout.WEST, consoleLogScroll, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, consoleLogScroll, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, consoleLogScroll, -15, SpringLayout.EAST, contentPane);
		contentPane.add(consoleLogScroll);
		
		txtInfoPlayer2 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer2, 56, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer2, 116, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer2, -25, SpringLayout.EAST, contentPane);
		txtInfoPlayer2.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer2.setText("Player 2 [pontos] objectivo");
		txtInfoPlayer2.setEditable(false);
		contentPane.add(txtInfoPlayer2);
		
		txtInfoPlayer4 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer4, 28, SpringLayout.SOUTH, txtInfoPlayer2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer4, -114, SpringLayout.NORTH, consoleLogScroll);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer4, -25, SpringLayout.EAST, contentPane);
		txtInfoPlayer4.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer4.setText("Player 4 [pontos] objectivo");
		txtInfoPlayer4.setEditable(false);
		contentPane.add(txtInfoPlayer4);
		
		txtInfoPlayer3 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer3, 3, SpringLayout.NORTH, txtInfoPlayer4);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer3, -111, SpringLayout.NORTH, consoleLogScroll);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer4, 64, SpringLayout.EAST, txtInfoPlayer3);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer3, -179, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer3, 521, SpringLayout.WEST, contentPane);
		txtInfoPlayer3.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer3.setText("Player 3 [pontos] objectivo");
		txtInfoPlayer3.setEditable(false);
		contentPane.add(txtInfoPlayer3);
		
		txtInfoPlayer1 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer1, -194, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer2, 79, SpringLayout.EAST, txtInfoPlayer1);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer1, -452, SpringLayout.SOUTH, contentPane);
		
		txtConsoleLog = new JTextArea();
		txtConsoleLog.setText("Welcome to A.I. Letter Game!");
		txtConsoleLog.setLineWrap(true);
		txtConsoleLog.setForeground(Color.WHITE);
		txtConsoleLog.setEditable(false);
		txtConsoleLog.setBackground(Color.BLACK);
		consoleLogScroll.setViewportView(txtConsoleLog);
		txtInfoPlayer1.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer1.setText("Player 1 [pontos] objectivo");
		txtInfoPlayer1.setEditable(false);
		contentPane.add(txtInfoPlayer1);
		
		lblConsoleLog = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblConsoleLog, -6, SpringLayout.NORTH, consoleLogScroll);
		lblConsoleLog.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblConsoleLog.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblConsoleLog.setBackground(Color.LIGHT_GRAY);
		lblConsoleLog.setText("Console Log:");
		contentPane.add(lblConsoleLog);
		
		lblPlayerSelection = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, lblConsoleLog, 0, SpringLayout.WEST, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayerSelection, 10, SpringLayout.NORTH, contentPane);
		lblPlayerSelection.setText("Player selection:");
		lblPlayerSelection.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblPlayerSelection.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblPlayerSelection.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayerSelection);
		
		lblInfoPlayer = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer1, 29, SpringLayout.SOUTH, lblInfoPlayer);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblInfoPlayer, 403, SpringLayout.EAST, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblInfoPlayer, 0, SpringLayout.NORTH, lblPlayerSelection);
		lblInfoPlayer.setText("Player's info:");
		lblInfoPlayer.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblInfoPlayer.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblInfoPlayer.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblInfoPlayer);
		
		lblPlayer1 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer1, 29, SpringLayout.SOUTH, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer1, 39, SpringLayout.WEST, contentPane);
		lblPlayer1.setBackground(Color.LIGHT_GRAY);
		lblPlayer1.setText("Player 1");
		contentPane.add(lblPlayer1);
		
		comboPlayer3 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer3, 106, SpringLayout.WEST, contentPane);
		comboPlayer3.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer3);
		
		lblPlayer2 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer2, 94, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPlayer1, -11, SpringLayout.NORTH, lblPlayer2);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer2, 0, SpringLayout.WEST, lblPlayer1);
		lblPlayer2.setText("Player 2");
		lblPlayer2.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer2);
		
		lblPlayer3 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer3, 39, SpringLayout.WEST, contentPane);
		lblPlayer3.setText("Player 3");
		lblPlayer3.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer3);
		
		lblPlayer4 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer4, 161, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPlayer3, -15, SpringLayout.NORTH, lblPlayer4);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer4, 0, SpringLayout.WEST, lblPlayer1);
		lblPlayer4.setText("Player 4");
		lblPlayer4.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer4);
		
		comboPlayer4 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer4, 18, SpringLayout.EAST, lblPlayer4);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, comboPlayer3, -4, SpringLayout.NORTH, comboPlayer4);
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboPlayer4, 156, SpringLayout.NORTH, contentPane);
		comboPlayer4.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer4);
		
		comboPlayer2 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer2, 18, SpringLayout.EAST, lblPlayer2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, comboPlayer2, -9, SpringLayout.NORTH, comboPlayer3);
		comboPlayer2.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer2);
		
		comboPlayer1 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.EAST, lblPlayer1, -18, SpringLayout.WEST, comboPlayer1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboPlayer1, 29, SpringLayout.SOUTH, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer1, 106, SpringLayout.WEST, contentPane);
		comboPlayer1.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer1);
		
		btnStartGame = new JButton("Start game!");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStartGame, 219, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnStartGame, -456, SpringLayout.EAST, contentPane);
		btnStartGame.addActionListener(this);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnStartGame, 77, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnStartGame, 0, SpringLayout.SOUTH, comboPlayer3);
		contentPane.add(btnStartGame);
		
		btnStopGame = new JButton("Stop game");
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer1, 47, SpringLayout.EAST, btnStopGame);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStopGame, 364, SpringLayout.WEST, contentPane);
		btnStopGame.addActionListener(this);
		btnStopGame.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnStopGame, 77, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnStopGame, 0, SpringLayout.SOUTH, comboPlayer3);
		contentPane.add(btnStopGame);
		*/
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
		
		if(action.equals("Start game!")) {
			doLog("Starting game..");
			myAgent.startGame();
			doLog("OK");
		} else if(action.equals("Stop game")) {
			doLog("Stopping game..");
			myAgent.stopGame();
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
