package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

import com.nightfall.awesomerogue.InGameState.Tile;

public class Enemy {

	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;
	
	public static final String[] enemyIcons = {"M", "r", "R", "Z", "S", "W"};
	
	private int x, y, health = 0;
	String name;
	String icon;
	
	public Enemy /* number one */ (int x, int y, int whichEnemy) {
		this.x = x;
		this.y = y;
		
		switch(whichEnemy) {
		case ANGRY_MUSHROOM:
			health = 5;
			name = "angry mushroom";
			break;
		case RAT:
			health = 10;
			name = "rat";
			break;
		case ZOMBIE:
			health = 30;
			name = "zombie";
			break;
		case SKELETON:
			health = 40;
			name = "skeleton";
			break;
		case WIZARD:
			health = 50;
			name = "ALLAN PLEASE PUT IN WIZARD NAME";
			//TODO: implement sweet wizard name maker
			break;
		}
		
		icon = enemyIcons[whichEnemy];
	}
	
	public void move(int dx, int dy, Tile[][] map) {
		x += dx;
		y += dy;
		
		//sanity check here.
	}
	
	public void forceMarch(int dx, int dy) {
		
		
	}
	
	//pathfinding is fun!
	public void pathToHeroAndMove(int targetX, int targetY, Tile[][] map) {
	}

	public void draw(Graphics2D g2) {
		g2.drawString(icon, (x*12 + InGameState.INGAME_WINDOW_OFFSET_X),
				(y*12 + 12 + InGameState.INGAME_WINDOW_OFFSET_X));	
	}
	public void getHit(int damage) {
		health -= damage;
		if(health <= 0) {
			System.out.println("The " + name + " is slain!");
		}
	}
}
