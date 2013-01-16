import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.Timer;

//The final boss, none other than Professor Z! He's like a much more horrible version of Ganondorf.
//He teleports around like a madman and is more than twice as fast. He talks random stuff and doesn't appear to be killable.
//The only way to beat level 5 is to kill all of the NullPointerExceptions while avoiding Professor Z and the Big Bloopers.
//He also doesn't care about physics.

public class ProfessorZ extends AnimatedCharacter {
	
	public String dialog;
	public Image sprite;
	public Timer insanityTimer;
	public Player player;
	public Random rand;
	public int dialogSwitcher;
	
	public ProfessorZ(Player p) {
		super (1000, 200, 80, 80);
		sprite = new ImageIcon("Game Pictures/ProfZ.jpg").getImage();
		rand = new Random();
		dialogSwitcher = 0;
		dialog = "If you can't compile in 0.00000002 nanoseconds, you're finished!";
		insanityTimer = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				x = rand.nextInt(1000);
				y = rand.nextInt(600);
				setDialog();
			}
		});
		insanityTimer.start();
		player = p;
		velocityY = 9;
		
	}
	
	public void setDialog() {
		switch (dialogSwitcher) {
		case 0: dialogSwitcher = 1; dialog = "I hope you like NullPointerExceptions without debug info."; break;
		case 1: dialogSwitcher = 2; dialog = "I promise Bloopers are NOTHING like quadtrees!"; break;
		case 2: dialogSwitcher = 3; dialog = "You're not expecting me to curve THIS for you, are you?"; break;
		case 3: dialogSwitcher = 4; dialog = "Ha ha ha, doesn't look like you have many free attempts left!"; break;
		case 4: dialogSwitcher = 5; dialog = "Oops, the tester's bugged, looks like you'll just have to die!"; break;
		case 5: dialogSwitcher = 1; dialog = "If you can't compile in 0.00000002 nanoseconds, you're finished!";
		}
	}
	
	@Override
	public void accelerate() {
		// TODO Auto-generated method stub
		int xSign = Integer.signum(x - player.x);
		int ySign = Integer.signum(y - player.y);
		velocityX = -xSign * 9;
		velocityY = -ySign * 9;
	}           

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawString("Health: ???", x + width + 5, y);
		g.drawString(dialog, x, y + height + 10);
		g.setColor(Color.WHITE);
		g.drawImage(sprite, x, y, width, height, null);
	}

	@Override
	public void setCurrentSprite() {
		// TODO Auto-generated method stub
		
	}
	

}
