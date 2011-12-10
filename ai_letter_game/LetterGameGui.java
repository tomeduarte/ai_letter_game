package ai_letter_game;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.SpringLayout;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JButton;

public class LetterGameGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8092527406687437334L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LetterGameGui frame = new LetterGameGui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LetterGameGui() {
		setTitle("AI Letter Game");
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
		
		JTextArea txtConsoleLog = new JTextArea();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtConsoleLog, -284, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtConsoleLog, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtConsoleLog, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtConsoleLog, -15, SpringLayout.EAST, contentPane);
		txtConsoleLog.setEditable(false);
		txtConsoleLog.setText("Lorem Ipsum Sit Dolor Amet");
		txtConsoleLog.setForeground(Color.WHITE);
		txtConsoleLog.setBackground(new Color(0, 0, 0));
		txtConsoleLog.setRows(20);
		contentPane.add(txtConsoleLog);
		
		JTextPane txtInfoPlayer2 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer2, 56, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer2, -105, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer2, 116, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer2, 0, SpringLayout.EAST, txtConsoleLog);
		txtInfoPlayer2.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer2.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer2.setEditable(false);
		contentPane.add(txtInfoPlayer2);
		
		JTextPane txtInfoPlayer4 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer4, 149, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer4, -75, SpringLayout.NORTH, txtConsoleLog);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer4, -15, SpringLayout.EAST, contentPane);
		txtInfoPlayer4.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer4.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer4.setEditable(false);
		contentPane.add(txtInfoPlayer4);
		
		JTextPane txtInfoPlayer3 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer3, 521, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer3, -75, SpringLayout.NORTH, txtConsoleLog);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer3, -179, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer4, 74, SpringLayout.EAST, txtInfoPlayer3);
		txtInfoPlayer3.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer3.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer3.setEditable(false);
		contentPane.add(txtInfoPlayer3);
		
		JTextPane txtInfoPlayer1 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer3, 33, SpringLayout.SOUTH, txtInfoPlayer1);
		sl_contentPane.putConstraint(SpringLayout.NORTH, txtInfoPlayer1, 0, SpringLayout.NORTH, txtInfoPlayer2);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtInfoPlayer1, 0, SpringLayout.WEST, txtInfoPlayer3);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtInfoPlayer1, -452, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, txtInfoPlayer1, -74, SpringLayout.WEST, txtInfoPlayer2);
		txtInfoPlayer1.setBackground(Color.LIGHT_GRAY);
		txtInfoPlayer1.setText("Player 1 [50] aaaaaa olamun");
		txtInfoPlayer1.setEditable(false);
		contentPane.add(txtInfoPlayer1);
		
		JTextPane lblConsoleLog = new JTextPane();
		lblConsoleLog.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblConsoleLog.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblConsoleLog.setBackground(Color.LIGHT_GRAY);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblConsoleLog, 0, SpringLayout.WEST, txtConsoleLog);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblConsoleLog, -6, SpringLayout.NORTH, txtConsoleLog);
		lblConsoleLog.setText("Console Log:");
		contentPane.add(lblConsoleLog);
		
		JTextPane lblPlayerSelection = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayerSelection, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayerSelection, 0, SpringLayout.WEST, txtConsoleLog);
		lblPlayerSelection.setText("Player selection:");
		lblPlayerSelection.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblPlayerSelection.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblPlayerSelection.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayerSelection);
		
		JTextPane lblInfoPlayer = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblInfoPlayer, 0, SpringLayout.NORTH, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblInfoPlayer, 0, SpringLayout.WEST, txtInfoPlayer3);
		lblInfoPlayer.setText("Player's info:");
		lblInfoPlayer.setForeground(UIManager.getColor("CheckBoxMenuItem.selectionBackground"));
		lblInfoPlayer.setFont(new Font("Helvetica", Font.PLAIN, 16));
		lblInfoPlayer.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblInfoPlayer);
		
		JTextPane lblPlayer1 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer1, 29, SpringLayout.SOUTH, lblPlayerSelection);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer1, 39, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblPlayer1, -433, SpringLayout.WEST, txtInfoPlayer1);
		lblPlayer1.setBackground(Color.LIGHT_GRAY);
		lblPlayer1.setText("Player 1");
		contentPane.add(lblPlayer1);
		
		JComboBox comboPlayer3 = new JComboBox();
		comboPlayer3.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer3);
		
		JTextPane lblPlayer2 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer2, 94, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPlayer1, -11, SpringLayout.NORTH, lblPlayer2);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer2, 0, SpringLayout.WEST, lblPlayer1);
		lblPlayer2.setText("Player 2");
		lblPlayer2.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer2);
		
		JTextPane lblPlayer3 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer3, 18, SpringLayout.EAST, lblPlayer3);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer3, 0, SpringLayout.WEST, lblPlayer1);
		lblPlayer3.setText("Player 3");
		lblPlayer3.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer3);
		
		JTextPane lblPlayer4 = new JTextPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPlayer4, 161, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblPlayer3, -15, SpringLayout.NORTH, lblPlayer4);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPlayer4, 0, SpringLayout.WEST, lblPlayer1);
		lblPlayer4.setText("Player 4");
		lblPlayer4.setBackground(Color.LIGHT_GRAY);
		contentPane.add(lblPlayer4);
		
		JComboBox comboPlayer4 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer4, 18, SpringLayout.EAST, lblPlayer4);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, comboPlayer3, -4, SpringLayout.NORTH, comboPlayer4);
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboPlayer4, 156, SpringLayout.NORTH, contentPane);
		comboPlayer4.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer4);
		
		JComboBox comboPlayer2 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer2, 18, SpringLayout.EAST, lblPlayer2);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, comboPlayer2, 0, SpringLayout.SOUTH, txtInfoPlayer2);
		comboPlayer2.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer2);
		
		JComboBox comboPlayer1 = new JComboBox();
		sl_contentPane.putConstraint(SpringLayout.WEST, comboPlayer1, 106, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, comboPlayer1, 0, SpringLayout.NORTH, txtInfoPlayer2);
		comboPlayer1.setBackground(Color.LIGHT_GRAY);
		contentPane.add(comboPlayer1);
		
		JButton btnStartGame = new JButton("Start game!");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnStartGame, 77, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStartGame, 61, SpringLayout.EAST, comboPlayer3);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnStartGame, 0, SpringLayout.SOUTH, comboPlayer3);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnStartGame, -187, SpringLayout.WEST, txtInfoPlayer1);
		contentPane.add(btnStartGame);
		
		JButton btnStopGame = new JButton("Stop game");
		btnStopGame.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnStopGame, 77, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnStopGame, 30, SpringLayout.EAST, btnStartGame);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnStopGame, 0, SpringLayout.SOUTH, comboPlayer3);
		contentPane.add(btnStopGame);
	}
}
