package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

import com.nightfall.awesomerogue.InGameState.Tile;

public class Character {
	public static final int VISIONRANGE = 25;
	
	private int x, y;
	private int awesome;
	
	public Character(int x, int y) {
		awesome = 100;
		this.x = x;
		this.y = y;
	}
	
	public void move(int dx, int dy, Tile[][] map) {
		int targetX = x + dx;
		int targetY = y + dy;
		
		if(map[targetX][targetY].blocker == false) {
			x = targetX;
			y = targetY;
		} else {
			System.out.println("You bump into a wall. Idiot.");
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getAwesome() { return awesome; }
	
	/**
	 * Draw the Character to the screen.
	 * 
	 * @param g2 The Graphics2D context that will be used to draw.
	 * @param camX Where the camera is horizontally.
	 * @param camY Where the camera is vertically.
	 */
	public void draw(Graphics2D g2, int camX, int camY) {
		g2.drawString("@", ((x-camX)*12+InGameState.INGAME_WINDOW_OFFSET_X), ((y-camY)*12+12+InGameState.INGAME_WINDOW_OFFSET_Y));
	}
}
