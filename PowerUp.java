import java.awt.Color;
import java.awt.Graphics;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

//Powerup class. This lists what happens when a powerup is constructed, and it's constructed relative to a block. Powerups will move
//to the left if on flat surfaces, and down the steps if they come from steps (as in addSteps in the Block class). The type of powerup
//is determined randomly.

public class PowerUp extends GameObject {

	public PowerUpType type;
	public PictureObj sprite;
	int index;
	
	public PowerUp(ItemBlock block) {
		super(block.x + 10, block.y - 30, block.d * 3, 3, 30, 30);
		
		double prob = Math.random();
		if (prob > 0.25 && prob <= 0.50) {
			type = PowerUpType.MUSHROOM;
			sprite = new PictureObj("Game Pictures/Mushroom.png");
		} else if (prob > 0.75 && prob <=0.90) {
			type = PowerUpType.ONEUP;
			sprite = new PictureObj("Game Pictures/1UP.png");
		} else if (prob > 0.90) {
			type = PowerUpType.STAR;
			width = 40;
			height = 40;
			sprite = new PictureObj("Game Pictures/Star.jpeg");
		} else { 
			type = PowerUpType.COIN;
			width = 20;
			height = 20;
			sprite = new PictureObj("Game Pictures/Coin.png"); 
		}
		
	}
	
	public void accelerate() {}
	
	public void draw(Graphics g) {
		g.drawImage(sprite.toImage(), x, y, width, height, null);
	}
	
}
