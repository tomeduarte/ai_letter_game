package ai_letter_game;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AgentInformation {

	private String 	name;
	private String 	word;
	private String 	letters;
	private int     points;
	private int     level;
	private int		player_id;
	private boolean	playing;

	/**
	 * UI Objects
	 */
	private JPanel	drawnPanel;
	private JButton removeButton;
	private JLabel	nameLabel;
	private JLabel	wordLabel;
	private JLabel	lettersLabel;
	private JLabel	pointsLabel;
	private JLabel	levelLabel;
	private JLabel	playingLabel;


	public AgentInformation(int id) {
		this.player_id = id;
	};

	public String getPlayer_id() {
		return String.valueOf(player_id);
	}
	public String getName() {
		return name;
	}
	public String getWord() {
		return word;
	}
	public String getLetters() {
		return letters;
	}
	public int getPoints() {
		return points;
	}
	public int getLevel() {
		return level;
	}
	public boolean isPlaying() {
		return playing;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public void setLetters(String letters) {
		this.letters = letters;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public JPanel getDrawnPanel() {
		return drawnPanel;
	}
	public JButton getRemoveButton() {
		return removeButton;
	}
	public JLabel getNameLabel() {
		if (nameLabel == null) {
			this.nameLabel = new JLabel(getName());
		}
		return nameLabel;
	}
	public JLabel getWordLabel() {
		if (wordLabel == null) {
			this.wordLabel = new JLabel(getWord());
		}
		return wordLabel;
	}
	public JLabel getLettersLabel() {
		if (lettersLabel == null) {
			this.lettersLabel = new JLabel(getLetters());
		}
		return lettersLabel;
	}
	public JLabel getPointsLabel() {
		if (pointsLabel == null) {
			this.pointsLabel = new JLabel(String.valueOf(getPoints()));
		}
		return pointsLabel;
	}
	public JLabel getLevelLabel() {
		if (levelLabel == null) {
			this.levelLabel = new JLabel(String.valueOf(getLevel()));
		}
		return levelLabel;
	}
	public JLabel getPlayingLabel() {
		if (playingLabel == null) {
			this.playingLabel = new JLabel(String.valueOf(isPlaying()));
		}
		return playingLabel;
	}
	public void setDrawnPanel(JPanel panel) {
		this.drawnPanel = panel;
	}
	public void setRemoveButton(ActionListener al) {
		this.removeButton = new JButton("remover");
		removeButton.setName(getPlayer_id());
		removeButton.addActionListener(al);
	}

	/**
	 * UI - Draw agent information on a given JPanel
	 * @param panel the panel to draw on
	 * @param agentId the agent identifier
	 */
	public void drawIn(JPanel panel, ActionListener listener) {
		setDrawnPanel(panel);
		setRemoveButton(listener);

		drawnPanel.add(getRemoveButton(), "sg delete");
		drawnPanel.add(getNameLabel(), "gap unrel, sg name");
		drawnPanel.add(getWordLabel(), "gap unrel, sg word");
		drawnPanel.add(getLettersLabel(), "gap unrel, sg letters");
		drawnPanel.add(getPointsLabel(), "gap unrel, sg points");
		drawnPanel.add(getLevelLabel(), "gap unrel, sg level");
		drawnPanel.add(getPlayingLabel(), "gap unrel, sg playing, wrap");

		drawnPanel.revalidate();
		drawnPanel.repaint();
		SwingUtilities.getRoot(drawnPanel).validate();
	}

	/**
	 * UI - remove agent information from the JPanel
	 */
	public void removeDrawnInformation() {
		drawnPanel.remove(getRemoveButton());
		drawnPanel.remove(getNameLabel());
		drawnPanel.remove(getWordLabel());
		drawnPanel.remove(getLettersLabel());
		drawnPanel.remove(getPointsLabel());
		drawnPanel.remove(getPlayingLabel());
		drawnPanel.remove(getLevelLabel());

		drawnPanel.revalidate();
		drawnPanel.repaint();
		SwingUtilities.getRoot(drawnPanel).validate();
	}
}