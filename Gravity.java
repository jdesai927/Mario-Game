
//Class that simulates gravity in my game world.

public class Gravity {
	
	final static double magnitude = 2;
	
	public static void pull (GameObject obj) {
			obj.velocityY += magnitude;
	}
}
