import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.Timer;

//Class for the boss of level 4. Ganondorf basically follows the player around and teleports to a random location if he hits a wall,
//along with some cool animation and sound effects. He'll also run away if the player gets a Mushroom or star, as he knows his own
//weakness.

public class Ganondorf extends AnimatedCharacter {

	Intersection currentCollision; //Checks if Ganondorf is hitting anything or not.
	boolean facingForward; //Checks whether Ganondorf is facing forward or not.
	Strategy strategy; //Ganondorf's current strategy. Determines his next course of action.
	Player player; //The player. Ganondorf monitors the player's behavior to create responses.
	public int speed; //Ganondorf's flying speed.
	public int health; //Ganondorf's health. When this is reduced to zero, Ganondorf will not die. It only helps set off the win condition.
	public boolean laughing; //Whether Ganondorf is laughing or not. Used to prevent the laughter sound effect from layering.
	public Timer teleportTimer; //Timer that determines when Ganondorf teleports.
	public Timer spinTimer; //Timer that blurs Ganondorf as he teleports.
	public PictureObj storedSprite; //Stores the original sprite so that it can be reset after being blurred during teleportation.
	public boolean isInvincible; //checks whether or not Ganondorf is invincible.
	
	public Ganondorf(Player p) {
		super(1000, 400, 45, 70);
		sprites = new PictureObj[] { new PictureObj("Game Pictures/Ganon_walking.gif"), new PictureObj ("Game Pictures/Ganon_flying.gif") };
		state = CharacterState.STANDING;
		setVelocity(-2, -2);
		currentCollision = Intersection.NONE;
		facingForward = false;
		strategy = Strategy.CHASEPLAYER;
		player = p;
		speed = 4;
		health = 5;
		laughing = false;
		isInvincible = false;
		//Initializes
		spinTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				velocityX = 0;
				velocityY = 0;
				if (!laughing) { new AePlayWave("GanondorfLaughin.wav").start(); laughing = true; }
				currentSprite = 1;
				storedSprite = new PictureObj("Game Pictures/Ganon_flying.gif");
				sprites[1] = Manipulate.blur(sprites[1], 5);
			}
		});
		teleportTimer = new Timer (4000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sprites[1] = storedSprite;
				teleport();
			}
		});
	}
	
	@Override
	public void setCurrentSprite() {
		if (velocityX == 0 && onSolidGround) {
			currentSprite = 0;
		}
	}
	
	//Checks if Ganondorf is colliding with any of the blocks. If he is, it stores which block he's colliding with in 'currentCollision'
	//in the form of an intersection.
	public boolean collisionWithBlock() {
		Iterator<Block> blks = PongCourt.blocks.iterator();
		while (blks.hasNext()) {
			Block blk = blks.next();
			Intersection i = this.collideByCoords(blk);
			if (i != Intersection.NONE) {
				currentCollision = i;
				return true;
			}
		}
		return false;
	}
	
	//Flies away from the player. He laughs maniacally when he reaches the top of the screen (and is therefore out of reach).
	public void moveAway() {
		int xSign = Integer.signum(x - 600);
		velocityX = -xSign * speed;
		velocityY = -speed;
		if (y == 0) { velocityX = xSign * speed; velocityY = 0; if (!laughing) { new AePlayWave("GanondorfLaughin.wav").start(); laughing = true; } } 
	} 
	
	//Teleports Ganondorf to a random location.
	public void teleport() {
		spinTimer.stop();
		Random rand = new Random();
		x = rand.nextInt(1000);
		y = rand.nextInt(600);
		collisionWithBlock();
		teleportTimer.stop();
	}
	
	//Moves toward the player. If he hits a wall, he laughs maniacally as he blurs and then teleports.
	public void moveToward() {
		int xSign = Integer.signum(x - player.x);
		int ySign = Integer.signum(y - player.y);
		if (collisionWithBlock()) { teleportTimer.start(); spinTimer.start(); } else {
			velocityX = -xSign * speed;
			velocityY = -ySign * speed;
		}
	}
	
	//Determine's Ganondorf's next move. If the player is invincible or has a Mushroom, Ganondorf will flee. Otherwise, he will charge at the player.
	public void nextMove() {
		if (teleportTimer.isRunning()) { return; } else { if (y != 0) { laughing = false; } }
		if (player.hasMushroom || player.invincible) { strategy = Strategy.AVOIDPLAYER; } else { strategy = Strategy.CHASEPLAYER; laughing = false; }
		switch (strategy) {
		case AVOIDPLAYER: moveAway(); break;
		case CHASEPLAYER: moveToward(); break;
		}
	}

	//Checks if Ganondorf is on the ground.
	public boolean onGround() {
		return (y + height >= 664);
	}
	
	@Override
	//Checks if Ganondorf is facing the right way or not and stores the answer in facingForward.
	public void accelerate() {
		facingForward = velocityX > 0;
		setCurrentSprite();
	}

	@Override
	//Draws Ganondorf.
	public void draw(Graphics g) {
		if (health < 0) { health = 0; } //Ensures that Ganondorf's health is not negative.
		PictureObj toDraw = sprites[currentSprite];
		if (!facingForward) { toDraw = Manipulate.mirrorHorizontal(toDraw); } //Mirrors the sprite if Ganondorf is facing the other way.
		g.drawString("Health: " + Integer.toString(health), x, y - 5); //Displays Ganondorf's health.
		g.drawImage(toDraw.toImage(), x, y, width, height, null);
	}
	
	
}
