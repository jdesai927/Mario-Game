import java.awt.Graphics;


//Invisible floor object for collision detection

public class Floor extends GameObject {

	public Floor(int y, int width) {
		super(0, y, 0, 0, width, 0);
	}
	
	public void accelerate () {}
	
	public void draw (Graphics g) {
		g.drawLine(0, y, width, y);
	}
	
	public boolean hitsFloor (AnimatedCharacter other) {
		return (other.y + other.height >= (664));
	}
	
}
