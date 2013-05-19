package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

public class Character {
	public static final int VISIONRANGE = 35;
	
	private int x, y;
	private int room;
	String character;
	
	private Weapon currentWeapon;
	
	public Character(int x, int y, String character) {
		initPos(x, y);
		this.character = character;
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
			map[targetX][targetY].doAction();
		}
		else {
			attack(entities[targetX][targetY]);
		}
	}
	
	public int getRoom() { return room; }
	public void setRoom(int room) { this.room = room; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public void setCurrentWeapon(Weapon weapon) { currentWeapon = weapon; }
	public Weapon getCurrentWeapon() { return currentWeapon; }
	
	private void attack(Character enemy) {
		if(enemy.getClass() == this.getClass()) return; // Friendly fire!
		currentWeapon.attack(enemy);
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
	}

	public String getName() {
		return character;
	}

	public void getHit(int damage) {
		System.out.println("I took "+damage+" damage but I don't know how to handle it");
	}
}
