import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

//Exactly like a block, except that it can release items when hit
public class ItemBlock extends Block {
	
	public int d; //Stores the direction of the power up. Ensures that it goes the right direction on release.
	
	public ItemBlock(int x, int y, int directionOfPowerUp) {
		super(x, y);
		d = directionOfPowerUp;
	}
	
	//Creates an item and adds it to the HashSet of powerups stored in PongCourt. Only does this if fewer than 15 powerups exist in the HashSet.
	public void releaseItem() {
		if (PongCourt.powerUpsInPlay.size() >= 15) { return; }
		new AePlayWave("smb_powerup_appears.wav").start();
		PongCourt.powerUpsInPlay.add(new PowerUp(this));
	}
	
	//Draws the ItemBlock
	public void draw (Graphics g) {
		Image i = new ImageIcon("Game Pictures/Questionblock.png").getImage();
		g.drawImage(i, x - 5, y - 5, width + 10, height + 10, null);
	}

}