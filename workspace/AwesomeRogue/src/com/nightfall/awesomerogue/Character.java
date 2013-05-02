package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

import com.nightfall.awesomerogue.InGameState.Tile;

public class Character {
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
	
	public void draw(Graphics2D g2) {
		g2.drawString("@", (x*12+InGameState.INGAME_WINDOW_OFFSET_X), (y*12+12+InGameState.INGAME_WINDOW_OFFSET_X));
	}
}
