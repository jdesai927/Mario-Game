
//My hierarchy is kind of messed up and I don't use it as effectively as I could've, but I use this abstract class to streamline the format
//I use for characters that have sprites and moving animations as well as simple physics.

public abstract class AnimatedCharacter extends GameObject {

	protected PictureObj[] sprites; //Array that holds all of the sprites of the animated character
	protected int currentSprite; //Index the array is accessed by when getting the sprite to be draw on this firing of tick()
	public CharacterState state; //State of the character.
	public boolean onSolidGround; //Whether the character is on the ground or not.
	
	public AnimatedCharacter(int x, int y, int width, int height) {
		super(x, y, 0, 0, width, height);
	}
	
	//This method is used for the physics of stock enemies.
	public void checkOnGround () {
		onSolidGround = onSolidGround || PongCourt.mainfloor.hitsFloor(this);
			if (this.onSolidGround) {
				if (this.velocityX == 0) {
					this.state = CharacterState.STANDING;
					this.setCurrentSprite();
				 } else { 
			 	    this.state = CharacterState.WALKING; 
					this.setCurrentSprite();
				}
			} else { Gravity.pull(this); }
			
	}
	
	//Abstract method to be implemented by subclasses. This is supposed to set the current sprite correctly based on the current
	//state of the player.
	public abstract void setCurrentSprite();
	
}
