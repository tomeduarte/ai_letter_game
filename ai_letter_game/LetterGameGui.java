package ai_letter_game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.SpringLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.ScrollPaneConstants;
import java.awt.Rectangle;

public class LetterGameGui extends JFrame implements ActionListener{

	private static final long serialVersionUID = -8092527406687437334L;
	private static AgentGameController myAgent; // Reference to the agent class

	/**
	 * UI components
	 */
	private JPanel contentPane;
	private JScrollPane consoleLogScroll;
	private JTextArea txtConsoleLog;
	private JTextPane txtInfoPlayer1;
	private JTextPane txtInfoPlayer2;
	private JTextPane txtInfoPlayer3;
	private JTextPane txtInfoPlayer4;
	private JTextPane lblConsoleLog;
	private JTextPane lblPlayerSelection;
	private JTextPane lblInfoPlayer;
	private JTextPane lblPlayer1;
	private JTextPane lblPlayer2;
	private JTextPane lblPlayer3;
	private JTextPane lblPlayer4;
	private JComboBox comboPlayer1;
	private JComboBox comboPlayer2;
	private JComboBox comboPlayer3;
	private JComboBox comboPlayer4;
	private JButton btnStartGame;
	private JButton btnStopGame;

	/**
	 * Create the frame.
	 */
	public LetterGameGui(AgentGameController agent) {
		myAgent = agent;
		
		setTitle("A.I. Letter Game");
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(260, 100, 800, 600);
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
		txtInfoPlayer2.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer2.setEditable(false);
		contentPane.add(txtInfoPlayer2);
		
		txtInfoPlayer4 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer4, 28, SpringLayout.SOUTH, txtInfoPlayer2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer4, -114, SpringLayout.NORTH, consoleLogScroll);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer4, -25, SpringLayout.EAST, contentPane);
		txtInfoPlayer4.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer4.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer4.setEditable(false);
		contentPane.add(txtInfoPlayer4);
		
		txtInfoPlayer3 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer3, 3, SpringLayout.NORTH, txtInfoPlayer4);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer3, -111, SpringLayout.NORTH, consoleLogScroll);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer4, 64, SpringLayout.EAST, txtInfoPlayer3);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer3, -179, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer3, 521, SpringLayout.WEST, contentPane);
		txtInfoPlayer3.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer3.setText("Player 1 [50] aaaaaa olamun");
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
		txtInfoPlayer1.setText("Player 1 [50] aaaaaa olamun");
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
	}

	/**
	 * Handle user actions
	 */
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		
		if(action.equals("Start game!")) {
			doLog("Starting game..");
			// switch enabled action buttons
			btnStartGame.setEnabled(false);
			comboPlayer1.setEnabled(false);
			comboPlayer2.setEnabled(false);
			comboPlayer3.setEnabled(false);
			comboPlayer4.setEnabled(false);
			btnStopGame.setEnabled(true);
			myAgent.startGame();
			doLog("OK");
		} else if(action.equals("Stop game")) {
			doLog("Stopping game..");
			// switch enabled action buttons
			btnStopGame.setEnabled(false);
			comboPlayer1.setEnabled(true);
			comboPlayer2.setEnabled(true);
			comboPlayer3.setEnabled(true);
			comboPlayer4.setEnabled(true);
			btnStartGame.setEnabled(true);
			myAgent.stopGame();
			doLog("OK");
		}
	}
	
	/**
	 *  Log messages to the Console Log
	 */
	protected void consoleLog(String message) {
		txtConsoleLog.append("\n" + message);
	}
	
	/**
	 * Log messages from the UI
	 */
	private void doLog(String message) {
		consoleLog("[UI] " + message);
	}
}
