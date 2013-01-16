import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.*;

@SuppressWarnings("serial")
public class PongCourt extends JPanel {
	public static int lives; //Variable that stores how many lives the player has left. The player loses the game if he/she dies with zero lives remaining.
	public static int score; //Stores the total score of the player. 10 points for each enemy killed, and 10 points for each coin collected.
	public static int level; //Stores the level the player is currently on.
	public int goombaCount; //The number of enemies the player must kill in order to advance to the next level.
	public static PictureObj bgImage; //Stores the background image.
	public static Color bgColor; //Stores the background color.
	public static HashSet<PowerUp> powerUpsInPlay; //HashSet that stores the powerups that have been released but not collected.
	public static HashSet<Goomba> goombas; //HashSet that stores the enemies that are still alive.
	public Player player; //The player's character.
	public static AnimatedCharacter boss; //If the level has an AnimatedCharacter boss, it is stored here. Otherwise, this variable is set to null.
	private int interval = 35; // Milliseconds between updates.
	public static Timer timer;       // Each time timer fires we animate one step.
	public static Timer timer2;		// I use this timer to help animate the player's running. 
									// Could easily have stored it in the Player class, but I don't think it makes a difference.
	public static Timer goombaGenTimer; //Timer that is responsible for spawning new enemies and checking for win/lose conditions.
	public static Timer musicTimer; //Timer that resets the music track.
	final static Floor mainfloor = new Floor(664, 1200); //Floor. I use this for player and enemy physics.
	public static HashSet<Block> blocks; //HashSet that stores all of the blocks on the level.
	public String backgroundSource; //Stores the filepath for the background music.
	private AePlayWave backgroundTheme; //Stores the thread for the background music.
	boolean starting; //Boolean that checks whether the game is just starting up or not. Used to prevent the game from executing tick() 
					  //or repaint() before everything has been initialized.
	public HashSet<BigBlooper> bloopers; //HashSet that stores the Big Bloopers in the level. Used only in level 5. I didn't have BigBlooper
										 //extend Goomba because I wanted a completely different kind of behavior, which was neither that of
										//boss nor of a stock enemy.

	final static JTextPane scoreBoard = new JTextPane(); //Textpane that is used to display the score
	final static JTextPane livesBoard = new JTextPane(); //Textpane to display lives remaining
	final static JTextPane levelBoard = new JTextPane(); //Textpane to display level
	
	final int COURTWIDTH = 1200; //Court width and height.
	final int COURTHEIGHT = 664;

	final int PADDLE_VEL = 4; //Now used for the player's running speed.

	public PongCourt() {
		setPreferredSize(new Dimension(COURTWIDTH, COURTHEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(true);

		timer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				tick(); 
			}});
		timer.start(); //Initializes and starts the main timer.

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					player.facingForward = false; //Turns the player around
					player.setVelocity(-PADDLE_VEL, player.velocityY);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					player.facingForward = true; //turns the player around
					player.setVelocity(PADDLE_VEL, player.velocityY);
				}
				else if (e.getKeyCode() == KeyEvent.VK_R) {
					level = 1; //Resets the game from level 1 with the same character.
					checkForLevel();
				}
				player.setCurrentSprite();
			}
			
			public void keyTyped(KeyEvent e) {
				//Prevents the player from jumping unless they are not already jumping and on the ground.
				if (e.getKeyChar() == ' ' && (player.onSolidGround) && player.state != CharacterState.JUMPING) {
					player.onSolidGround = false;
					if (player.jumpcount%3 == 0) { new AePlayWave(player.sfx[1]).start(); } else
					if (player.jumpcount%3 == 1) { new AePlayWave(player.sfx[2]).start(); } else
					if (player.jumpcount%3 == 2) { new AePlayWave(player.sfx[3]).start(); }
					player.jumpcount++;
					player.state = CharacterState.JUMPING;
					player.setCurrentSprite();
					player.setVelocity(player.velocityX, ((player.jumpcount - 1) %3 + 1) * (-10));
				}
				//Key to skip to the last level. For my convenience.
				if (e.getKeyChar() == 'a') { 
					level = 4;
					checkForLevel();
				}
				
			}
			
			public void keyReleased(KeyEvent e) {
				//Stops the player if he/she releases the left or right key.
				if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) {
					player.setVelocity(0, player.velocityY);
					if (player.state != CharacterState.JUMPING) { player.state = CharacterState.STANDING; }
				}		
				player.setCurrentSprite();
			}
		});
		
		//Initializes the player animation timer
		timer2 = new Timer(300, new ActionListener() {
			public void actionPerformed (ActionEvent e) { tick2(); }
		});
		
		//Initializes and starts the background music reset timer
		musicTimer = new Timer(10, new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (backgroundTheme == null || backgroundSource == null) { return; }
				if (backgroundTheme.nBytesRead == -1) {
					backgroundTheme = new AePlayWave(backgroundSource);
				}
			}
		});
		musicTimer.start();
		
		Goomba.speedMultiplier = 1;
		goombaCount = 10;
		goombas = new HashSet<Goomba>();
		blocks = new HashSet<Block>();
		powerUpsInPlay = new HashSet<PowerUp>();
		backgroundSource = "DocksTheme.wav";
		
		starting = false;
		
		//Initializes and starts the player's powerup timer. This will bring the player back to his normal state 15 seconds after using a powerup.
		Player.powerUpTimer = new Timer(15000, new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				if (player == null) { return; }
				if (player.hasMushroom || player.invincible) {
					new AePlayWave("smb3_powerdown.wav").start();
					player.height = player.originalHeight;
					player.width = player.originalWidth;
					player.hasMushroom = false;
					player.invincible = false;
				}
			}});
		
		Player.powerUpTimer.start();
		
		lives = 3;
		score = 0;
		level = 1;
		bgColor = Color.BLACK;
		
		// After a PongCourt object is built and installed in a container
		// hierarchy, somebody should invoke reset() to get things started... 
	}	
	
	//Method that creates a Player named "Mario" if the player chooses to play as Mario.
	public void playerMario() {
		player = new Player("Mario", 20, COURTHEIGHT - 10, 30, 60, new String[] { "Game Pictures/Mario_standing.png",
				 																  "Game Pictures/Mario_running.png",
				 																  "Game Pictures/Mario_jumping.png", 
																				  "Game Pictures/Mario-Dead-icon.png" },
																   new String[] { "mario-herewego.WAV", 
				 																  "mario-wa.WAV", 
				 																  "mario-haha.WAV", 
				 																  "mario-yippee.WAV",
				 																  "mario-oof.WAV",
				 																  "mario-fire.WAV" } );
	}
	
	//If the player chooses Bowser, creates a player named Bowser.
	public void playerBowser() {
		player = new Player("Bowser", 20, COURTHEIGHT - 10, 40, 60, new String[] { 
				 "Game Pictures/Bowser_walk1.gif",
				 "Game Pictures/Bowser_walk2.gif",
				 "Game Pictures/Bowser_jump.jpg",
				 "Game Pictures/Bowser_jump.jpg"
				}, 
							new String[] { 
				 							"bowser-inhale.WAV",
				 							"bowser-something.WAV",
					 						"bowser-grab.WAV",
					 						"bowser-fire.WAV",
					 						"bowser-grab.WAV",
					 						"bowser-die.WAV"
				});
	}

	//Prompts the player for who they would like to play as. Does not let the user proceed until they choose either Mario or Bowser.
	public String promptForCharacter() {
		String result = "";
		while (result == null || (!result.equals("Mario") && !result.equals("Bowser"))) {
		result = (String)JOptionPane.showInputDialog(
				null,
				"Who will you play as?",
				"Choose a character.",
				JOptionPane.PLAIN_MESSAGE,
				null,
				new String[] { "Mario", "Bowser" },
				"Mario" );
		}
		return result;
	}
	
	//Loads the first level.
	public void startLevel1() { 
		bgImage = new PictureObj("Game Pictures/Mario_level.jpg");
		String result;
		result = promptForCharacter();
		if (result.equals("Mario")) { playerMario(); } else  { playerBowser(); }
		timer.start();
		blocks = new HashSet<Block>();
		Block.addHorizontalLine(15, 200, 450);
		Block.addHorizontalLine(15, 100, 300);
		Block.addHorizontalLine(15, 200, 150);
		Goomba.speedMultiplier = 1;
		goombas = new HashSet<Goomba>();
		lives = 3;
		score = 0;
		level = 1;
		bgColor = Color.BLACK;
		powerUpsInPlay = new HashSet<PowerUp>();
		backgroundSource = "Super Mario Bros. Theme Song.wav";
		backgroundTheme = new AePlayWave("Super Mario Bros. Theme Song.wav");
		backgroundTheme.start();
		new AePlayWave(player.sfx[0]).start();
		
		//Initializes and starts the main enemy generator and win/lose condition checking timer. Has different conditions for different levels.
		goombaGenTimer = new Timer(5000, new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (goombas == null) { return; }
				if (bloopers == null) { bloopers = new HashSet<BigBlooper>(); }
				if (level == 5) {
					if (goombas.size() <= 6) { 
						goombas.add(new Goomba(600, 0, new String[] { Goomba.goombaSprite1,
								  Goomba.goombaSprite2 }));
					}
					if (bloopers.size() <= 5) {
						bloopers.add(new BigBlooper(player));
					}
					if (Math.random() > 0.8 && goombas.size() == 0) {
						checkForLevel();
					}
					
				}
				if (goombas.size() <= goombaCount ) {
					goombas.add(new Goomba(600, 0, new String[] { Goomba.goombaSprite1,
																  Goomba.goombaSprite2 }));
				} else {
					Iterator<Goomba> goombit = goombas.iterator();
					boolean allDead = true;
					while (goombit.hasNext()) {
						if (goombit.next().state != CharacterState.DEAD) { allDead = false; break; }
					}
					if (allDead) {
						if (level == 4) {
							Ganondorf bos = (Ganondorf) boss;
							if (bos.health > 0) { return; }
						}
						level++; 
						goombaGenTimer.stop(); 
						checkForLevel(); }
				}
			}
		});
		goombaGenTimer.start();
		
		//Sets the sprites of the stock enemies to the picture of the Goomba.
		Goomba.goombaSprite1 = "Game Pictures/Goomba_walking.jpg";
		Goomba.goombaSprite2 = "Game Pictures/Goomba_walking.jpg";
		
		starting = true;
		grabFocus();
		
	}
	
	
	
	//Method that is responsible for directing the player to the next level. If the player beats level 5, he/she wins the game.
	public void checkForLevel() {
		if (backgroundTheme != null) { backgroundTheme.nBytesRead = -1; }
		timer.stop();
		if (player != null) {
			backgroundTheme = new AePlayWave("Stage Clear.wav");
			backgroundTheme.start();
			while (backgroundTheme.isAlive()) {}
		}
		powerUpsInPlay = new HashSet<PowerUp>();
		boss = null;
		bloopers = null;
		if (level - 1 != 0) { JOptionPane.showMessageDialog(null, "Level " + (level - 1) + " cleared!"); }
		goombaCount = 10;
		if (level == 1) { startLevel1(); return; }
		if (player.name.equals("Mario")) { playerMario(); } else { playerBowser(); }
		goombas = new HashSet<Goomba>();
		if (level == 2) { startLevel2(); return; }
		if (level == 3) { startLevel3(); return; }
		if (level == 4) { startLevel4(); return; }
		if (level == 5) { startLevel5(); return; }
		new AePlayWave("mario-theend.wav").start();
		JOptionPane.showMessageDialog(null, "Congratulations! You have won the game with a score of " + score + ". Press 'R' or click the Reset button to play again.");
		goombaGenTimer.stop();
		
	}
	
	//Loads level 2
	public void startLevel2() {
		bgImage = new PictureObj("Game Pictures/Ocean_level.jpg");
		blocks = new HashSet<Block>();
		Block.addSteps(5, 1, 400, 450);
		blocks.add(new Block(650, 200));
		Block.addSteps(5, -1, 900, 450);
		backgroundSource = "DocksTheme.wav";
		backgroundTheme = new AePlayWave(backgroundSource);
		backgroundTheme.start();
		boss = new BigBlooper(player);
		Goomba.goombaSprite1 = "Game Pictures/blooper2.png";
		Goomba.goombaSprite2 = "Game Pictures/blooper1.png";
		goombaGenTimer.start();
		timer.start();
		grabFocus();
	}
	
	//Loads level 3
	public void startLevel3() {
		bgImage = new PictureObj("Game Pictures/Pokemon_stadium.jpg");
		timer.start();
		blocks = new HashSet<Block>();
		Block.addPokeBall(400, 200);
		backgroundSource = "Pokemon Red & Blue - Opening (music).wav";
		backgroundTheme = new AePlayWave(backgroundSource);
		backgroundTheme.start();
		Goomba.goombaSprite1 = "Game Pictures/pika1.png";
		Goomba.goombaSprite2 = "Game Pictures/pika2.png";
		Goomba.speedMultiplier = 7;
		boss = null;
		goombaGenTimer.start();
		timer.start();
		grabFocus();
	}
	
	//Loads level 4
	public void startLevel4() {
		bgImage = new PictureObj("Game Pictures/Ganons_castle.jpg");
		backgroundSource = "Ganondorf Battle.wav";
		blocks = new HashSet<Block>();
		Block.addSteps(8, 1, 200, 450);
		Block.addSteps(7, -1, 900, 450);
		Block.addHorizontalLine(9, 350, 450);
		Block.addHorizontalLine(5, 450, 300);
		backgroundTheme = new AePlayWave(backgroundSource);
		backgroundTheme.start();
		Goomba.goombaSprite1 = "Game Pictures/DarkLink.png";
		Goomba.goombaSprite2 = "Game Pictures/DarkLink.png";
		Goomba.speedMultiplier = 3;
		boss = new Ganondorf(player);
		goombaGenTimer.start();
		timer.start();
		grabFocus();
	}
	
	//Loads level 5
	public void startLevel5() {
		bgImage = new PictureObj("Game Pictures/Final_destination.jpg");
		backgroundSource = "Final Destination.wav";
		backgroundTheme = new AePlayWave(backgroundSource);
		backgroundTheme.start();
		bloopers = new HashSet<BigBlooper>();
		blocks = new HashSet<Block>();
		boss = new ProfessorZ(player);
		Goomba.speedMultiplier = 5;
		Block.addHorizontalLine(15, 100, 475);
		goombaGenTimer.start();
		timer.start();
		grabFocus();
	}
	
	//Animates the player character's running
	void tick2() {
		if (!player.onSolidGround) {
			return;
		}
		//Swaps sprites
		if (player.state == CharacterState.WALKING) {
			if (player.currentSprite == 2) { player.currentSprite = 0; } else { player.currentSprite = 2; }
		}
	}

	//Method called when the main timer fires.
	void tick() {
		if (!starting) { return; }
		//Checks for losing condition.
		if (lives < 0) {
			JOptionPane.showMessageDialog(null, "Game over! Final score: " + Integer.toString(score));
			level = 1;
			checkForLevel();
			return;
		}
		
		//Maps out the movements, collisions, and interactions of each of the objects in the game.
		
		player.setBounds(getWidth(), getHeight());
		Iterator<Goomba> goombit;
		if (goombas != null) { goombit = goombas.iterator(); } else { return; }
		player.move();
		if (bloopers != null) {
			Iterator<BigBlooper> bloopit = bloopers.iterator();
			while (bloopit.hasNext()) {
				BigBlooper bloop = bloopit.next();
				bloop.setBounds(getWidth(), getHeight());
				bloop.move();
				Intersection bloopin = bloop.collideByCoords(player);
				if (bloopin != Intersection.NONE) {
					if (!player.invincible  && !player.hasMushroom) {
						player.death();
					}
				}
			}
		}
		if (boss != null && player != null) { 
			if (level == 2) {
				boss.setBounds(getWidth(), getHeight());
				boss.move();
				if (boss.collideByCoords(player) != Intersection.NONE && !player.invincible) {
					player.death();
				}
			} else if (level == 4) {
				Ganondorf b = (Ganondorf) boss;
				b.setBounds(getWidth(), getHeight());
				b.nextMove(); 
				b.move(); 
				if (b.collideByCoords(player) != Intersection.NONE) {
					if (player.hasMushroom) { 
						b.health--;
						if (b.health == 0) { 
							//boss death
						} 
					} else if (!player.invincible) { player.death(); }
				}
			}
			else if (level == 5) {
					ProfessorZ prof = (ProfessorZ) boss;
					prof.setBounds(getWidth(), getHeight());
					prof.move();
					if (prof.collideByCoords(player) != Intersection.NONE) {
						if (!player.hasMushroom && !player.invincible) { player.death(); }
					}
				}
			}
		while (goombit.hasNext()) {
			Goomba goomba = goombit.next();
			goomba.setBounds(getWidth(), getHeight());
			if (goomba.state != CharacterState.DEAD) {
				goomba.move();
				Iterator<Block> blks = blocks.iterator();
				while (blks.hasNext()) {
					Block blk = blks.next();
					goomba.collideByCoords(blk);
				}
				Gravity.pull(goomba);
				if(!player.invincible) {
					Intersection mn = player.collideByCoords(goomba);
					if (mn == Intersection.DOWN || (player.hasMushroom && mn != Intersection.NONE)) { goomba.state = CharacterState.DEAD; goomba.setCurrentSprite(); } 
					else if (mn != Intersection.NONE) {
						player.death();
						player.invincible = true;
					}
				}
			}
		}
  		if (powerUpsInPlay != null) {
  			Iterator<PowerUp> pups = powerUpsInPlay.iterator();
  			while (pups.hasNext()) {
  				PowerUp pow = pups.next();
  				pow.setBounds(getWidth(), getHeight());
  				Gravity.pull(pow);
  				Iterator<PowerUp> powitOther = powerUpsInPlay.iterator();
  				while (powitOther.hasNext()) {
  					PowerUp powpow = powitOther.next();
  					pow.collideByCoords(powpow);
  				}
  				pow.move();
  				boolean onTheGround = false;
  				Iterator<Block> blks = blocks.iterator();
  				while (blks.hasNext()) {
  					Intersection m = pow.collideByCoords(blks.next());
  					if (m == Intersection.DOWN) { onTheGround = true; break; }
  				}
  				if (!(onTheGround || pow.y + pow.height <= COURTHEIGHT)) { pow.velocityX = 0; }
  				Intersection pickUp = player.collideByCoords(pow);
  				if (pickUp != Intersection.NONE) {
  					player.usePowerUp(pickUp, pow);
  					pups.remove();
  					powerUpsInPlay.remove(pow);
  				}
  			}
  		}
  		Iterator<Block> blks = blocks.iterator();
 		while(blks.hasNext()) { 
 			Block blk = blks.next();
 			Intersection i = player.collideByCoords(blk);
			if (i == Intersection.DOWN) {
				player.onSolidGround = true;
				break;
			} else { player.onSolidGround = player.onSolidGround || false; }
		}
		player.checkOnGround();
		
		//Updates the score, lives, and level display.
		scoreBoard.setText("Score: " + Integer.toString(score));
		livesBoard.setText("Lives: " + Integer.toString(lives));
		levelBoard.setText("Level: " + Integer.toString(level));
		repaint(); // Repaint indirectly calls paintComponent.
	}

	public void paintComponent(Graphics g) {
		if (!starting) { return; }
		super.setBackground(bgColor);
		super.paintComponent(g); // Paint background, border
		if (bgImage != null) { g.drawImage(bgImage.toImage(), 0, 0, 1200, 800, null); } //Paints the background
		
		//Draws all of the game objects
		
		Iterator<Block> blks = blocks.iterator();
		while (blks.hasNext()) {
			blks.next().draw(g);
		}
		if (powerUpsInPlay != null) {
			Iterator<PowerUp> pups = powerUpsInPlay.iterator();
			while(pups.hasNext()) {
				pups.next().draw(g);
			}
		}
		Iterator<Goomba> goombit;
		if (goombas == null) { return; }
		goombit = goombas.iterator();
		player.draw(g);
		while (goombit.hasNext()) {
			Goomba goomba = goombit.next();
			goomba.draw(g);
		}
		if (boss != null) {
			boss.draw(g);
		}
		if (bloopers != null) {
			Iterator<BigBlooper> bloopit = bloopers.iterator();
			while (bloopit.hasNext()) {
				bloopit.next().draw(g);
			}
		}
		
	}
}
