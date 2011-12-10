package ai_letter_game;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;

public class LetterGameGui extends JFrame {

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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 646, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTextArea txtrLoremIpsumSit = new JTextArea();
		txtrLoremIpsumSit.setEditable(false);
		txtrLoremIpsumSit.setText("Lorem Ipsum Sit Dolor Amet");
		txtrLoremIpsumSit.setForeground(Color.WHITE);
		txtrLoremIpsumSit.setBackground(new Color(0, 0, 0));
		txtrLoremIpsumSit.setRows(15);
		contentPane.add(txtrLoremIpsumSit, BorderLayout.SOUTH);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(20);
		flowLayout.setHgap(20);
		panel.setBackground(Color.YELLOW);
		contentPane.add(panel, BorderLayout.EAST);
	}

}
