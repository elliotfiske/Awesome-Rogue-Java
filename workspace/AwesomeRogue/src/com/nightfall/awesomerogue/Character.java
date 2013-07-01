package com.nightfall.awesomerogue;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class Character {
	public static final int VISIONRANGE = 35;
	
	private int x, y;
	private int room;
	String character;
	
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
		else {
			attack(new Point(dx, dy));
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
		InGameState.waitOn("animation");
		
		if(inAir) altitude ++;
	}
	
	public void update(Tile[][] map, Character[][] entities) {
		if(drawingAttack) {
			currentWeapon.update(map, entities);
		}
		
		if(forceMarch) {
			int targetX = x;
			int targetY = y;
			// Calculate how far we want to move!
			if(forceMarchTo.x < x) {
				targetX --;
			}
			else if(forceMarchTo.x > x) {
				targetX ++;
			}
			
			if(forceMarchTo.y < y) {
				targetY --;
			}
			else if(forceMarchTo.y > y) {
				targetY ++;
			}
			
			if(!map[targetX][targetY].blocker && entities[targetX][targetY] == null ||
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
			}
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
	
	public void die() { dead = true; }

	public boolean dead() {
		return dead;
	}

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		// TODO Auto-generated method stub
	}
	
	public void takeTurn(MainCharacter mainChar, Tile[][] map, Character[][] entities) {
		
	}
}
