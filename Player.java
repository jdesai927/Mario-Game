import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

//Class that represents the player character and some of its important functionalities.

public class Player extends AnimatedCharacter {

	public PictureObj[] sprites; //Array of PictureObjs that stores each of the sprites.
	public String name;
	public int totalSprites;
	public int currentSprite; //The sprite to be displayed during this firing of tick().
	public int interval;
	public int originalWidth; //Stores the original width and height of the player. Used to transform back to normal size after the Mushroom times out.
	public int originalHeight;
	public CharacterState state; //Stores the current state of the character.
	boolean facingForward; //Checks whether the player is facing forward or not.
	public int jumpcount; //Checks how many times the player has jumped. Used for the triple jump functionality (see the Instructions text).
	public boolean onSolidGround; //Checks whether the player is on the ground or not. Used for physics.
	public String showDialog; //The String that stores what the player is saying. May change based on the situation, or may be null.
	public static Timer dialogTimer; //The timer that sets showDialog to null after some amount of time.
	public static Timer powerUpTimer; //See PongCourt class for initialization and explanation.
	public static Timer respawnTimer; //When the player dies, he becomes invincible for a short period of time. When this timer fires, the player returns to normal.
	public boolean hasMushroom; //Checks whether the player is under the effects of the Super Mushroom.
	public boolean invincible; //Checks whether the player is invincible or not. Could be because of the star, or recent death.
	public String[] sfx; //Stores the filepaths of the player-specific sound effects.

	public Player(String name, int x, int y, int width, int height, String[] urls, String[] sfx) {
		super(x, y, width, height);
		interval = 50;
		this.name = name;
		sprites = new PictureObj[4];
		for (int i = 0; i < sprites.length; i++) {
			sprites[i] = new PictureObj(urls[i]);
		}
		//Because two Mario sprites are facing the wrong way, I mirror them here
		if (name.equals("Mario")) {
			sprites[1] = Manipulate.mirrorHorizontal(sprites[1]);
			sprites[3] = Manipulate.mirrorHorizontal(sprites[1]);
		}
		originalWidth = width;
		originalHeight = height;
		currentSprite = 0;
		facingForward = true;
		state = CharacterState.STANDING;
		jumpcount = 0;
		onSolidGround = true;
		dialogTimer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent e) { showDialog = null; }});
		dialogTimer.start();
		respawnTimer = new Timer(4000, new ActionListener() {
			public void actionPerformed(ActionEvent e) { invincible = false; respawnTimer.stop(); }
		});
		this.sfx = new String[sfx.length];
		for (int k = 0; k < sfx.length; k++) {
			this.sfx[k] = sfx[k];
		}
		PongCourt.timer2.start();
		hasMushroom = false;
		invincible = false;
	}
	
	//Executes a set of responses to the player dying, including a sound effect, reducing the number of lives, and making the player invincible.
	public void death() {
		PongCourt.lives--;
		new AePlayWave(sfx[5]).start();
		invincible = true;
		respawnTimer.start();
		showDialog = "OWW!";
	}
	
	public void setCurrentSprite () {
		switch (state) {
		case STANDING: currentSprite = 0; break;
		case JUMPING: currentSprite = 1;  break;
		case DEAD: currentSprite = 3; 
		}
	}
	
	public void accelerate() {}
	
	//On collision with a powerup, uses it and causes a sound effect. See "Instructions" for powerup effects.
	public void usePowerUp(Intersection i, PowerUp o) {
		
		if (i == Intersection.NONE) { return; }
		if (!hasMushroom) {
			switch (o.type) {
			case MUSHROOM:
				if (!invincible) {
					new AePlayWave("smb_powerup.wav").start();
					width = originalWidth * 2;
					height = originalHeight * 2;
					hasMushroom = true;
					showDialog = "BIG " + name.toUpperCase() + "!";
					powerUpTimer.restart();
				}
				break;
			case STAR:
				if (!hasMushroom) {	
					invincible = true;
					showDialog = "Invincible!";
					powerUpTimer.restart();
				}
				break;
			case ONEUP:
				new AePlayWave("smb_1-up.wav").start();
				showDialog = "1UP!";
				PongCourt.lives++;
				break;
			case COIN:
				PongCourt.score += 10;
				new AePlayWave("smb_coin.wav").start();
			}
		}	
		
	}
	
	//Updates whether or not the player is on the ground.
	public void checkOnGround () {
		
		onSolidGround = onSolidGround || y + height >= 664;
			if (this.onSolidGround) {
				if (this.velocityX == 0) {
					this.state = CharacterState.STANDING;
					this.setCurrentSprite();
				 } else { 
			 	    this.state = CharacterState.WALKING; 
				}
			}
			
			Gravity.pull(this);
			
	}
	
	public void draw(Graphics g) { //Animation!
		
		PictureObj toDraw = sprites[currentSprite];
		//Flips the player if he's facing the wrong way.
		if (facingForward) {
			toDraw = Manipulate.mirrorHorizontal(toDraw);
		}
		//Draws the dialog.
		if (showDialog != null) {
			g.setColor(Color.WHITE);
			g.drawString(showDialog, x, y - 5);
		}
		//Draws the player.
		g.drawImage(toDraw.toImage(), x, y, width, height, null);
	}
	
}
