import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;

import javax.imageio.ImageReader;
import javax.swing.JOptionPane;
import javax.swing.Timer;

//Class for the stock enemy. Simple enemy that moves and turns around if it hits a wall. Will die if you jump on it,
//will kill the player if it makes contact in any other way. However, if the player has a mushroom, contact with the enemy
//means death for the enemy.

public class Goomba extends AnimatedCharacter {
	
	public static Timer flipTime; //Timer that flips the sprite. Used for the Goomba animation.
	public static Timer dialogTimer; //Timer that displays dialog, if the Goomba has anything to say.
	public static Timer deathTimer; //Timer that removes the Goomba on death.
	public int currentSprite; //Index by which the array of sprites should be accessed.
	public static String goombaSprite1; //Filepaths for the sprites. Static so that they can be changed in PongCourt to suit different levels.
	public static String goombaSprite2;
	public static int goombaWidth = 36; //Height and width of each Goomba. Again, can be modified externally.
	public static int goombaHeight = 36;
	public int speed; //Goomba speed. Is modified in different levels to change the difficulty.
	boolean facingForward; //Checks if the enemy is facing forward, and mirrors it as necessary if it is.
	public static int speedMultiplier; //Multiplies the speed if necessary.
	
	public Goomba(int x, int y, String[] urls) {
		super(x, y, goombaWidth, goombaHeight);
		sprites = new PictureObj[2];
		int i;
		for (i = 0; i < sprites.length; i++) {
			sprites[i] = new PictureObj(urls[i]);
		}
		if (PongCourt.level == 1) { sprites[1] = Manipulate.mirrorHorizontal(sprites[0]); }
		flipTime = new Timer(500, new ActionListener() {
			//Swaps the sprites every half a second.
			public void actionPerformed(ActionEvent e) { if (currentSprite == 1) { currentSprite = 0; } else { currentSprite = 1; }}});
		flipTime.start(); 
		
		state = CharacterState.WALKING;
		
		//Removes enemies from the HashSet stored in PongCourt.
		deathTimer = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				if (state == CharacterState.DEAD) { PongCourt.goombas.remove(this); }
			}
		});
		facingForward = false;
		deathTimer.start();
		speed = speedMultiplier * (int) Math.signum(0.5 - Math.random());
		setVelocity(speed, 0);
		currentSprite = 0;
	}
	
	@Override
	public void setCurrentSprite() {
		switch (state) {
		case WALKING: 
			setVelocity(speed, 0);
			currentSprite = 0;
		}
		
	}

	@Override
	public void accelerate() {
		// TODO Auto-generated method stub
		
	}

	//Draws the enemy
	@Override
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		if (state == CharacterState.DEAD) { return; }
		PictureObj toDraw = sprites[currentSprite];
		if (velocityX > 0) { facingForward = true; } else { facingForward = false; }
		if (facingForward) { toDraw = Manipulate.mirrorHorizontal(toDraw); }
		if (PongCourt.level == 5) {
			//Draws a string that says "NullPointerException!" if it is level 5.
			g.drawString("NullPointerException!", x, y);
			return;
		}
		g.drawImage(toDraw.toImage(), x, y, width, height, null);
	}
	
	

}
