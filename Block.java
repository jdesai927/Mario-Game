import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.ImageIcon;

//This class is a template for a solid block that the player cannot destroy or move through. The block cannot move and has a collider itself,
//but is not affected by gravity. 

public class Block extends GameObject {
	
	final static int WIDTH = 50;
	final static int HEIGHT = 50;
	
	public Block(int x, int y) {
		super(x, y, 0, 0, WIDTH, HEIGHT);
	}
	
	//Adds steps to the HashSet of blocks stored in PongCourt
	public static void addSteps(int numBlocks, int orientation, int startingX, int startingY) {
		int i = 0;
		while (numBlocks > 0) {
			if (Math.random() < 0.75) { PongCourt.blocks.add(new Block(startingX + (50 * orientation * i), startingY - (50 * i))); } else
			{ PongCourt.blocks.add(new ItemBlock(startingX + (50 * orientation * i), startingY - (50 * i), -orientation)); }
			numBlocks--;
			i++;
		}
	}
	
	//Adds a horizontal line of bricks to the HashSet stored in PongCourt given a length and leftmost point.
	public static void addHorizontalLine(int numBlocks, int startingX, int startingY) {
		int i = 0;
		while (numBlocks > 0) {
			if (Math.random() < 0.75) { PongCourt.blocks.add(new Block(startingX + 50 * i, startingY)); } else
			{ PongCourt.blocks.add(new ItemBlock(startingX + 50 * i, startingY, -1)); }
			i++;
			numBlocks--;
		}
	}
	
	//Adds the arrangement of bricks in level 3 to the HashSet stored in the PongCourt.
	public static void addPokeBall (int startingX, int startingY) {
		PongCourt.blocks.add(new Block(startingX + 150, startingY + 150));
		PongCourt.blocks.add(new Block(startingX + 200, startingY + 100));
		PongCourt.blocks.add(new Block(startingX + 150, startingY + 100));
		PongCourt.blocks.add(new Block(startingX + 200, startingY + 150));
		addSteps(3, -1, startingX + 100, startingY + 275);
		addSteps(3, 1, startingX, startingY + 75); 
		addSteps(3, 1, startingX + 250, startingY + 275);
		addSteps(3, -1, startingX + 350, startingY + 75);
	}
	
	public void accelerate() {} //Blocks do not need to move
	
	//Draws the block.
	public void draw (Graphics g) { 
		g.drawImage(new ImageIcon("Game Pictures/Brick.png").getImage(), x, y, width, height, null);
	}
	
}
