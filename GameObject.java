import java.awt.Graphics;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public abstract class GameObject {
	int x; // x and y coordinates upper left
	int y;

	int width;
	int height;

	double velocityX; // Pixels to move each time move() is called.
	double velocityY;

	int rightBound; // Maximum permissible x, y values.
	int bottomBound;
	
	public GameObject(int x, int y, double velocityX, double velocityY, int width,
			int height) {
		this.x = x;
		this.y = y;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.width = width;
		this.height = height;
	}

	public void setBounds(int width, int height) {
		rightBound = width - this.width;
		bottomBound = height - this.height;
	}

	public void setVelocity(double velocityX, double velocityY) {
		this.velocityX = velocityX;
		this.velocityY = velocityY;
	}

	// Move the object at the given velocity.
	public void move() {
		x += velocityX;
		y += velocityY;
		accelerate();
		clip();
	}

	//Alternative collision scheme that uses coordinate checking to determine the direction of the collision.
	public Intersection collideByCoords(GameObject other) {
		int down = y + height;
		int right = x + width;
		int odown = other.y + other.height;
		int oright = other.x + other.width;
		if (down > other.y && down < odown && x < oright && right > other.x) { clip(other, Intersection.DOWN); return Intersection.DOWN; }
		if (y < odown && y > other.y && x < oright && right > other.x) { clip(other, Intersection.UP); return Intersection.UP; }
		if (right < oright && right > other.x && down > other.y && y < odown) { clip(other, Intersection.LEFT); return Intersection.LEFT; }
		if (x > other.x && x < oright && down > other.y && y < odown) { clip(other, Intersection.RIGHT); return Intersection.RIGHT; }
		return Intersection.NONE;
	}
	
	//Turns things that hit the sides around.
	public void pivot () {
		if (!(this instanceof Player)) {
			velocityX = -velocityX;
		}
	}
	
	//Keeps colliding objects from overlapping. Also responsible for handling some collisions (like between player and ItemBlock)
	public void clip (GameObject other, Intersection i) {
		int odown = other.y + other.height;
		int oright = other.x + other.width;
		switch (i) {
		case DOWN: 
			y = other.y - height; 
			if (this instanceof Player && other instanceof Goomba) { 
				velocityY =  -velocityY / 2; 
				PongCourt.score += 10;
				return; 
			}
			velocityY = 0; 
			break;
		case UP: 
			if (this instanceof Player && other instanceof ItemBlock) {
				Player p = (Player) this;
				ItemBlock ib = (ItemBlock) other;
				p.showDialog = "DING!";
				ib.releaseItem();
			}
			y = odown; 
			velocityY = 0; 
			break;
		case RIGHT: 
			x = oright; 
			if (!(this instanceof Player) && !(other instanceof Player)) { other.velocityX = - other.velocityX; velocityX = -velocityX; } else { velocityX = 0; }
			break;
		case LEFT: 
			x = other.x - width; 
			if (!(this instanceof Player) && !(other instanceof Player)) { other.velocityX = - other.velocityX; velocityX = -velocityX; } else { velocityX = 0; }
		}
	}
	
	// Keep the object in the bounds of the court. Also plays a sound effect and makes new dialog text if the player hits the side.
	public void clip() {
		if (x < 0) {
	 		while (x < 10) {
				x += 2;
			} 
			if (this instanceof Player) { velocityX = 0; }
			pivot();
			if (this instanceof Player) {
				Player pl = (Player) this;
				new AePlayWave(pl.sfx[4]).start();
				pl.showDialog = "OOF!";
			}
		} else if (x > rightBound) {
			while (x > rightBound - 10) {
				x -= 2;
			}
			if (this instanceof Player) { velocityX = 0; }
			pivot();
			if (this instanceof Player) {
				Player pl = (Player) this;
				new AePlayWave(pl.sfx[4]).start();
				pl.showDialog = "OOF!";
			}
		}
		if (y < 0) {
			y = 0;
		} else if (y > bottomBound) {
			y = bottomBound;
		}
	}

	/**
	 * Compute whether two GameObjects intersect.
	 * 
	 * @param other
	 *            The other game object to test for intersection with.
	 * @return NONE if the objects do not intersect. Otherwise, a direction
	 *         (relative to <code>this</code>) which points towards the other
	 *         object.
	 */
	
	public Intersection intersects(GameObject other) {
		if (       other.x > x + width
				|| other.y > y + height
				|| other.x + other.width  < x
				|| other.y + other.height < y)
			return Intersection.NONE;
		
		
		// compute the vector from the center of this object to the center of
		// the other
		double dx = other.x + other.width /2 - (x + width /2);
		double dy = other.y + other.height/2 - (y + height/2);

		double theta = Math.atan2(dy, dx);
		double diagTheta = Math.atan2(height, width);

		if ( -diagTheta <= theta && theta <= diagTheta )
			return Intersection.RIGHT;
		if ( diagTheta <= theta && theta <= Math.PI - diagTheta )
			return Intersection.DOWN;
		if ( Math.PI - diagTheta <= theta || theta <= diagTheta - Math.PI )
			return Intersection.LEFT;
		// if ( diagTheta - Math.PI <= theta && theta <= diagTheta)
		return Intersection.UP;
	}

	public abstract void accelerate();

	public abstract void draw(Graphics g);
}
