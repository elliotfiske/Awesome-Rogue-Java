package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

public class Character {
	public static final int VISIONRANGE = 35;
	
	private int x, y;
	private int room;
	String character;
	
	public Character(int x, int y, String character) {
		initPos(x, y);
		this.character = character;
	}
	
	public void initPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move(int dx, int dy, Tile[][] map) {
		
		int targetX = x + dx;
		int targetY = y + dy;
		
		if(!map[targetX][targetY].blocker) {
			x = targetX;
			y = targetY;
			room = map[x][y].room;
		}
		
		// Do action for the tile you tried to walk to.
		// That way we can have impassible tiles that
		// Can be interacted with.
		map[targetX][targetY].doAction();
	}
	
	public int getRoom() { return room; }
	public void setRoom(int room) { this.room = room; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	/**
	 * Draw the Character to the screen.
	 * 
	 * @param g2 The Graphics2D context that will be used to draw.
	 * @param camX Where the camera is horizontally.
	 * @param camY Where the camera is vertically.
	 */
	public void draw(Graphics2D g2, int camX, int camY) {
		g2.drawString("@", ((x-camX)*12), ((y-camY)*12+12));
	}
}
