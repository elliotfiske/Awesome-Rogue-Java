package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class Character {
	public static final int VISIONRANGE = 35;
	
	protected int x;
	protected int y;
	
	private int room;
	String character = "default character?!?";
	
	private int altitude;	// 0 is default, meaning it's on the ground
	
	private boolean forceMarch;
	private Point forceMarchTo;
	
	private Weapon currentWeapon;
	private boolean drawingAttack;
	
	private boolean dead;
	
	public Character(int x, int y, String character) {
		initPos(x, y);
		this.character = character;
		
		dead = false;
	}
	
	public void initPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move(int dx, int dy, Tile[][] map, Character[][] entities) {
		int targetX = x + dx;
		int targetY = y + dy;
		
		if(!map[targetX][targetY].blocker && entities[targetX][targetY] == null) {
			entities[x][y] = null;
			x = targetX;
			y = targetY;
			room = map[x][y].room;
			entities[targetX][targetY] = this;
		}
		else if(entities[targetX][targetY] == null) {
			// Do action for the tile you tried to walk to.
			// That way we can have impassible tiles that
			// Can be interacted with.
			// Only do action if there's no enemy there though.
			map[targetX][targetY].doAction(this);
		}
		else if(entities[targetX][targetY] instanceof Enemy){
			attack(new Point(dx, dy));
		} else {
			System.out.println("Your " + entities[targetX][targetY].getName() + " is in the way.");
		}
	}
	
	public int getRoom() { return room; }
	public void setRoom(int room) { this.room = room; }

	public int getAltitude() { return altitude; }
	public void setAltitude(int altitude) { this.altitude = altitude; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public void setCurrentWeapon(Weapon weapon) { currentWeapon = weapon; }
	public Weapon getCurrentWeapon() { return currentWeapon; }
	
	// Set the weapon to attack in a certain direction.
	// This does not do any damage inherently, in case
	// You punch the air or something. The weapon handles that.
	public void attack(Point direction) {
		
		//if(enemy.getClass() == this.getClass()) return; // Friendly fire!
		drawingAttack = true;
		InGameState.waitOn("animation");
		
		// Tell the weapon both where you are attacking from and what
		// DIrection to attack in
		currentWeapon.attack(new Point(x, y), direction);
	}

	public void forceMarch(int dx, int dy) {
		forceMarch(dx, dy, false);
	}
	
	public void forceMarch(int dx, int dy, boolean inAir) {
		forceMarch = true;
		forceMarchTo = new Point(x + dx, y + dy);
		//Just in case, remove other forcemarches from this guy
		InGameState.endAllWaits("forcemarch" + getName() + getID());
		InGameState.waitOn("forcemarch" + getName() + getID());
		System.out.println("Force march from " + x + ", " + y + " to " + forceMarchTo.x + ", " + forceMarchTo.y);
		
		if(inAir) altitude ++;
	}
	
	public void update(Tile[][] map, Character[][] entities) {
		if(drawingAttack) {
			currentWeapon.update(map, entities);
		}
		
		if(forceMarch) {
			int proposedX = x;
			int proposedY = y;
			
			/** Rough estimate of speed based on how far they're gonna go */
			int speed = Math.abs(forceMarchTo.x - x) + Math.abs(forceMarchTo.y - y);
			
			// Calculate how far we want to move!
			if(forceMarchTo.x < x) {
				proposedX --;
			}
			else if(forceMarchTo.x > x) {
				proposedX ++;
			}
			
			if(forceMarchTo.y < y) {
				proposedY --;
			}
			else if(forceMarchTo.y > y) {
				proposedY ++;
			}
			
			if(map[proposedX][proposedY].blocker) {
				//you hit a wall ouuuch
				if(this instanceof MainCharacter) {
					System.out.println("You slam into a wall!");
				}
				
				if(this instanceof Enemy) {
					System.out.println("The " + getName() + " slams into a wall!");
				}

				altitude = 0;
				InGameState.endWait("forcemarch" + getName() + getID());
				forceMarch = false;
				System.out.println("Line 136?");
			}
			
			if(entities[proposedX][proposedY] != null && forceMarch && entities[proposedX][proposedY] != this) {
				//We just slammed into somebody.  LOOKS LIKE THEY'RE COMIN' ALONG FOR THE RIDE
				//Consider their weight, though.  Inelastic collision!
				int myWeight = getWeight();
				int hisWeight = entities[proposedX][proposedY].getWeight();
				
				//If they're huge, they won't get knocked back as far.  Will never go below 1, though.
				int newSpeed = (int) (speed - Math.floor((double) hisWeight / (double) myWeight));
				if(newSpeed < 1) { newSpeed = 1; }
				
				//Now we gotta recalculate our target points.
				//First, reverse engineer the direction.
				Point direction = new Point((int) Math.signum(forceMarchTo.x - x), (int) Math.signum(forceMarchTo.y - y));
				//Now, move the guy we ran into to this new target! (direction * speed)
				entities[proposedX][proposedY].forceMarch(direction.x * newSpeed, direction.y * newSpeed);
				//Meanwhile, adjust our target to one behind the other guy's.
				forceMarch(direction.x * (newSpeed-1), direction.y * (newSpeed-1));
				System.out.println("line 155? Other guy: " + entities[proposedX][proposedY].getName());
			}
			
			//Have we arrived at our destination?
			if(forceMarchTo.x == x && forceMarchTo.y == y) {
				//Feel free to move about the cabin
				InGameState.endWait("forcemarch" + getName() + getID());
				forceMarch = false;
				altitude = 0;
				System.out.println("line 163?");
			}
			
			//I guess we have no choice left but to move :P
			if(forceMarch) {
				entities[x][y] = null;
				entities[proposedX][proposedY] = this;
				x = proposedX;
				y = proposedY;
				System.out.println("line 171?");
			}
			
			/*if((!map[targetX][targetY].blocker && entities[targetX][targetY] == null) ||
					entities[targetX][targetY].getAltitude() != altitude) {
				entities[x][y] = null;
				x = targetX;
				y = targetY;
				room = map[x][y].room;
				entities[targetX][targetY] = this;
			}
			else {
				if(map[x][targetY].blocker || entities[x][targetY] != null ||
						entities[x][targetY].getAltitude() != altitude) {
					targetY = y;
					forceMarchTo.y = y;
				}
				if(map[targetX][y].blocker || entities[targetX][y] != null ||
						entities[targetX][y].getAltitude() != altitude) {
					targetX = x;
					forceMarchTo.x = x;
				}
				// Try to move again
				if(!map[targetX][targetY].blocker && entities[targetX][targetY] == null ||
						entities[targetX][targetY].getAltitude() != altitude) {
					entities[x][y] = null;
					x = targetX;
					y = targetY;
					room = map[x][y].room;
					entities[targetX][targetY] = this;
				}
			}
			
			if(x == forceMarchTo.x && y == forceMarchTo.y) {
				InGameState.endWait("animation");
				forceMarch = false;
				if(altitude > 0) altitude --;
			}*/
		}
	}
	
	/**
	 * Draw the Character to the screen.
	 * 
	 * @param g2 The Graphics2D context that will be used to draw.
	 * @param camX Where the camera is horizontally.
	 * @param camY Where the camera is vertically.
	 */
	public void draw(Graphics2D g2, int camX, int camY) {
		g2.drawString(character, ((x-camX)*12), ((y-camY)*12+12));
		if(drawingAttack) {
			if(!currentWeapon.draw(g2, camX, camY)) {
				drawingAttack = false;
				InGameState.endWait("animation");
			}
		}
	}

	public String getName() {
		return character;
	}

	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		System.out.println("I took "+damage+" damage but I don't know how to handle it");
	}
	
	public void die() { 
		dead = true; 
		Character[][] entities = InGameState.getEntities();
		entities[x][y] = null; 
	}

	public boolean dead() {
		return dead;
	}

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		// TODO Auto-generated method stub
	}
	
	public void takeTurn(MainCharacter mainChar, Tile[][] map, Character[][] entities) {
		
	}
	
	/**
	 * How heavy you are.  Important for collisions.
	 */
	public int getWeight() {
		//Defaults to 10
		return 10;
	}
	
	/**
	 * Calculates the difference between a character and this one and propels the other one away.
	 * @param c The character to push away.
	 */
	public void knockAway(Character c, int distance) {
		int dx = c.getX() - x;
		int dy = c.getY() - y;
		
		dx = (int) Math.signum((double) dx);
		dy = (int) Math.signum((double) dy);
		
		c.forceMarch(dx * distance, dy * distance);
	}
	
	/**
	 * Uses the last 3 characters of the default toString() to make a unique id.
	 * @return A sweet, sweet unique ID
	 */
	public String getID() {
		return toString().substring(toString().length() - 3);
	}
}
