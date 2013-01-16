import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//GUI class.

public class Game {

	private Game() {
		// Top-level frame
		final JFrame frame = new JFrame("Pong");
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Main playing area
		final PongCourt court = new PongCourt();
		frame.add(court, BorderLayout.CENTER);

		//adding score, level, and lives
		final JPanel mainText = new JPanel();
		mainText.add(PongCourt.scoreBoard, BorderLayout.EAST);
		mainText.add(PongCourt.livesBoard, BorderLayout.CENTER);
		mainText.add(PongCourt.levelBoard, BorderLayout.WEST);
		frame.add(mainText, BorderLayout.SOUTH);
		
		//Subpanel for all of the additional buttons
		final JPanel subPanel = new JPanel();
		final JButton reset = new JButton("Reset");
		//Resets the game from level 1
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PongCourt.level = 1;
				court.checkForLevel();
			}
		});
		//Restarts the level
		final JButton restartLevel = new JButton("Restart Level");
		restartLevel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PongCourt.lives = 3;
				PongCourt.score = 0;
				court.checkForLevel();
			}
		});
		//Lists the level objectives
		final JButton levelObjectives = new JButton("Objectives");
		levelObjectives.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String instructions = "";
				switch (PongCourt.level) {
				case 1: 
					instructions += 
						"Destroy all the Goombas to advance to the next level!";
					break;
				case 2:
					instructions +=
						"Destroy the Bloopers and avoid the Big Blooper to advance to the next level!";
					break;
				case 3:
					instructions +=
						"Avoid the nimble Pikachus and pick them off one by one to advance to the next level!";
					break;
				case 4:
					instructions +=
						"Slaughter The Great King of Evil Ganondorf's minions and crush him to advance to the next level!";
					break;
				case 5: 
					instructions +=
						"Fight to the finish! Can you defeat the crazed Professor Z while fending off his minions?";
				}
				JOptionPane.showMessageDialog(frame, instructions);
				court.grabFocus();
			}
		});
		
		//Displays the game's instructions
		final JButton instructions = new JButton("Instructions");
		instructions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PongCourt.timer.stop();
				JOptionPane.showMessageDialog(frame, 
						"Welcome to Super 120 Bros.! My game is a 2D action game that involves using powerups to kill enemies and bosses.\n" +
						"Click the 'Objectives' button at any point to see what the objective for that level is. Beware! Viewing the objectives window will not pause the game.\n" +
						"Use the left and right arrow keys to move your player, and the spacebar to jump.\n" +
						"Jumping on top of most weaker enemies will kill them, but you may have to use other means to defeat stronger enemies.\n" +
						"Beware! The world of Mario and Bowser is fraught with danger. Contact with an enemy (excluding the above) will cause you to lose a life.\n" +
						"Your jump will increase in height each time you jump, every third jump being the highest, and then reset to the original jump height.\n" +
						"Hitting the blocks with the ?s on them will generate a powerup, which you may use to your advantage.\n" +
						"The star will render your player invulnerable for a short period of time.\n" +
						"The Super Mushroom will increase your player's size and allow him to defeat enemies he otherwise wouldn't have strength to defeat, while also making him virtually invulnerable.\n" +
						"The Green Mushroom will given you an extra life.\n" +
						"The Coin will increase your score by 10 points.\n" +
						"The Mushroom and the Star last for only a short time each. Use them wisely!\n" +
						"You will also gain points for each enemy you kill.\n" +
						"Other cool features:\n" +
						"-special boss AIs for the later levels\n" +
						"-sprite animation and a complete set of sound effects for two playable characters\n" +
						"-levels designed in tribute to some of the greatest games ever\n" +
						"-different sets of animated enemies and objectives for each level\n" +
						"-fully functioning physics engine\n" +
						"-complete set of sound effects for pretty much everything that happens in-game\n" +
						"\n" +
						"Good luck and enjoy!");
				PongCourt.timer.start();
				court.grabFocus();
			} 
		});
		
		//Quit button
		final JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		subPanel.add(instructions, BorderLayout.SOUTH);
		subPanel.add(restartLevel, BorderLayout.WEST);
		subPanel.add(quit, BorderLayout.EAST);
		subPanel.add(levelObjectives, BorderLayout.NORTH);
		subPanel.add(reset, BorderLayout.CENTER);
		final JPanel panel = new JPanel();
		panel.add(subPanel, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.NORTH);

		// Put the frame on the screen
		frame.pack();
        frame.setVisible(true);
		// Start the game running
        court.startLevel1();
        		
		}

	/*
	 * Rather than directly building the top level frame object in the main
	 * method, we use the invokeLater utility method to ask the Swing framework
	 * to invoke the method 'run' of the Runnable object we pass it, at some
	 * later time that is convenient for it. (The key technical difference is
	 * that this will cause the new object to be created by a different
	 * "thread".)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Game();
			}
		});
	}
}