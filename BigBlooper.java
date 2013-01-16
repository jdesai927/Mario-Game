import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.Timer;

//Another enemy class. I didn't have this extend Goomba because I wanted a different kind of behavior.
//This is a big enemy that can't be killed, he just annoys the hell out of the player and threatens him.

public class BigBlooper extends AnimatedCharacter {

	public static Timer animationTimer; //Changes the sprite for the animation
	public static Timer homingTimer; //Changes velocity to move towards the player's position every so often
	public Player player; //Stores the player to monitor its behavior
	
	public BigBlooper(Player p) {
		super(1000, 400, 100, 100);
		currentSprite = 0;
		sprites = new PictureObj[2];
		sprites[0] = new PictureObj("Game Pictures/blooper1.png");
		sprites[1] = new PictureObj("Game Pictures/blooper2.png");
		player = p;
		animationTimer = new Timer (500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentSprite == 1) { currentSprite = 0; } else { currentSprite = 1; }
			}
		});
		animationTimer.start();
		homingTimer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int xSign = Integer.signum(x - player.x);
				int ySign = Integer.signum(y - player.y);
				velocityX = 3 * -xSign;
				velocityY = 3 * -ySign;
			}
		});
		homingTimer.start();
		velocityX = -4;
		velocityY = -4;
	}
	
	//If the blooper hits a wall, it is made to move in a random direction perpendicular to the collision for a little while
    //before it homes in on the player again.
	
	public void accelerate() {
		Iterator<Block> blockit = PongCourt.blocks.iterator();
		Intersection in = Intersection.NONE;
		boolean x = false;
		while (blockit.hasNext()) {
			Block b = blockit.next();
			in = this.collideByCoords(b);
			if (in != Intersection.NONE) {
				x = true; 
				break;
			}
		}
		if (x) { 
			if (in == Intersection.LEFT || in == Intersection.RIGHT) {
				velocityX = -velocityX;
				velocityY *= Math.signum(0.5 - Math.random());
				return;
			}
			if (in == Intersection.UP || in == Intersection.DOWN) { 
				velocityX *= Math.signum(0.5 - Math.random());
				velocityY = -velocityY;
				return;
			}
		}
	}
	
	public void setCurrentSprite() {
		
	}
	
	//Draws the BigBlooper.
	public void draw(Graphics g) {
		g.drawImage(sprites[currentSprite].toImage(), x, y, width, height, null);
	}
	
}
