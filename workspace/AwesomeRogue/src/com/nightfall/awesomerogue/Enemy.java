package com.nightfall.awesomerogue;

public class Enemy {

	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;
	
	private int x, y, health = 0;
	String name;
	
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
	}
	
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}
	
	public void forceMarch(int dx, int dy) {
		
		
	}

	
	//pathfinding is fun!
	public void findPathToHero(int targetX, int targetY) {
		//nab our own copy of the map.
		
	}
	
}
