package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Enemy {

	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;

	public static final String[] enemyIcons = {"M", "r", "R", "Z", "S", "W"};

	private int x, y, whichEnemy, health = 0;
	String name;
	String icon;

	public Enemy /* number one */ (int x, int y, int whichEnemy) {
		this.x = x;
		this.y = y;

		this.whichEnemy = whichEnemy;

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

	//Fuzzy pathfinding = fun times for all!
	public void pathToHeroAndMove(int targetX, int targetY, Tile[][] map) {
		//SO. The first step in my fun little "fuzzy pathfinding" is to just draw a straight line from the enemy to the hero.

		//Coordinates of the line that walks to the player.
		int straightX = x;
		int straightY = y;
		
		while(!(straightX == targetX && straightY == targetY)) {
			//Calculate which direction it would be smart to go in order to walk to the player.
			Point delta = walkStraight(straightX, straightY, targetX, targetY);

			//System.out.println("sx: " + straightX + ", sy: " + straightY + ", tx: " + targetX + ", ty: " + targetY);
			
			straightX += delta.x;
			straightY += delta.y;
			
			//try {
				map[straightX][straightY].illustrate(Color.yellow);
			//} catch(ArrayIndexOutOfBoundsException e) {}
		}

	}

	/**
	 * Handy helper method.  Calculates the direction an enemy should logically take
	 * to walk STRAIGHT from (x, y) to (targetX, targetY).
	 * @param x Start x
	 * @param y Start y
	 * @param targetX Destination x
	 * @param targetY Destination y
	 * @return Point with x from -1 --> 1 saying you should move that far in the x direction, 
	 * 		   and y from -1 --> 1 saying you should move that far in the y direction
	 */
	public Point walkStraight(int x, int y, int targetX, int targetY) {
		Point result = new Point(-100,-100);

		int diffX = targetX - x;
		int diffY = targetY - y;
		
		System.out.println("diffX: " + diffX + ", diffY: " + diffY);

		//Figure out the slope to the target
		//First, prevent /0 error:
		if(diffX == 0 && diffY < 0) {
			result.x = 0;
			result.y = 1;
			return result;
		}

		if(diffX == 0 && diffY > 0) {
			result.x = 0;
			result.y = -1;
			return result;
		}

		double slope = (double) diffY / diffX;

		//I hand-calculated these values, on some sweet graph paper.
		//Come look at it sometime.

		//  |      ^
		//  |  or  |
		//  v      |
		if(slope <= -2) {
			result.x = 0;
			result.y = -1 * (int) Math.signum((float) diffY);
		}

		//    /        ^ 
		//   /   or   /
		//  L        /
		if(-2 < slope && slope < -0.5) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = -1 * (int) Math.signum((float) diffY);
		}

		//
		// < - -   or  - - >
		//
		if(-0.5 <= slope && slope <= 0.5) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 0;
		}

		// ^       \
		//  \  or   \
		//   \       V
		if(0.5 < slope && slope < 2) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		//  |      ^
		//  |  or  |
		//  v      |
		if(slope >= 2) {
			result.x = 0;
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		return result;
	}

	/**
	 * Render the enemy to the screen.
	 * 
	 * @param g2 The Graphics2D handle it uses to draw itself.
	 * @param camX Camera X offset
	 * @param camY Camera Y offset
	 */
	public void draw(Graphics2D g2, int camX, int camY) {
		g2.drawString(icon, ((x - camX)*12 + 2),
				((y - camY)*12 + 10));	
	}

	/**
	 * Enemies have hit points (unlike the player's "Awesome level.")
	 * 
	 * This takes them away.  It also check's if they're dead.
	 * 
	 * @param damage How much damage to do to the monster.
	 */
	public void getHit(int damage) {
		health -= damage;
		if(health <= 0) {

			if(whichEnemy == WIZARD) {
				//win
			}

			System.out.println("The " + name + " is slain!");
		}
	}
}
