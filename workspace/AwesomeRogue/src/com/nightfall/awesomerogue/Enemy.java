package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Enemy extends Character {

	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;
	public static final int MUSHROOM = 6;

	public static final String[] enemyIcons = {"M", "r", "R", "Z", "S", "W", "m"};

	private int x, y, whichEnemy, health = 0;
	String name;
	String icon;

	public Enemy /* number one */ (int x, int y, int whichEnemy) {
		super(x, y, enemyIcons[whichEnemy]);
		this.x = x;
		this.y = y;

		this.whichEnemy = whichEnemy;

		switch(whichEnemy) {
		case ANGRY_MUSHROOM:
			health = 15;
			name = "angry mushroom";
			break;
		case MUSHROOM:
			health = 1;
			name = "mushroom";
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
		
		setCurrentWeapon(new EnemyWeapon(whichEnemy));
	}

	public void move(int dx, int dy, Tile[][] map) {
		x += dx;
		y += dy;

		//sanity check here.
	}
	
	public int getX() { return x; }
	public int getY() { return y; }

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		pathToHeroAndMove(mainChar.getX(), mainChar.getY(), map);
	}

	//Fuzzy pathfinding = fun times for all!
	public void pathToHeroAndMove(int targetX, int targetY, Tile[][] map) {
		return;
		//SO. The first step in my fun little "fuzzy pathfinding" is to just draw a straight line from the enemy to the hero.

		//Coordinates of the line that walks to the player.
//		int straightX = x;
//		int straightY = y;
//
//		/** List of tiles straight from the monster to the player. */
//		ArrayList<Tile> straightTiles = new ArrayList<Tile>();
//
//		while(!(straightX == targetX && straightY == targetY)) {
//			//Calculate which direction it would be smart to go in order to walk to the player.
//			Point delta = walkStraight(straightX, straightY, targetX, targetY);
//
//			System.out.println("sx: " + straightX + ", sy: " + straightY + ", tx: " + targetX + ", ty: " + targetY);
//
//			straightX += delta.x;
//			straightY += delta.y;
//
//			straightTiles.add(map[straightX][straightY]);
//
//			if(map[straightX][straightY].blocker) {
//				map[straightX][straightY].illustrate(Color.red);
//			} else {
//				map[straightX][straightY].illustrate(Color.yellow);
//			}
//		}
//
//		for(int whichTile = 0; whichTile < straightTiles.size(); whichTile++) {
//			//
//			Tile t = straightTiles.get(whichTile);
//
//			//Go through until we run into sexy trouble (blocker)
//			if(t.blocker) {
//				//OH NO! Blocker found.  Send out "feelers" to go along right and left walls.
//				Point rightFeeler = new Point(straightTiles.get(whichTile - 1).x, straightTiles.get(whichTile - 1).y);
//				Point leftFeeler = new Point(rightFeeler.x, rightFeeler.y);
//
//				//figure out which direction they start off in.
//				//Right:
//				rightFeelerDirection = getIntialDirection(rightFeeler, new Point(t.x, t.y), true, map);
//
//				//Left:
//				leftFeelerDirection = getIntialDirection(leftFeeler, new Point(t.x, t.y), false, map);
//
//				int numTiles = 0;
//				while(numTiles < 20) {
//					//follow right wall
//					Point delta = followTheWall(rightFeeler.x, rightFeeler.y, true, rightFeelerDirection, map);
//					rightFeeler.translate(delta.x, delta.y);
//
//					map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue);
//					
//					//follow left wall
//					delta = followTheWall(leftFeeler.x, leftFeeler.y, false, leftFeelerDirection, map);
//					leftFeeler.translate(delta.x, delta.y);
//					
//					map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);
//					
//					//TODO: lol check if we're "Backontrack" 
//					
//					numTiles++;
//				}
//			}
//		}

	}	

	/**
	 * Which direction should the "feelers" go to start?
	 * 
	 * Directions:
	 * 7 0 1
	 * 6 X 2
	 * 5 4 3
	 * 
	 * 
	 * @param feeler Point describing where the "feeler" is
	 * @param wall Point describing the wall the "feeler" is up against
	 * @param goingRight True if this is a rightwards feeler, false if it is a leftwards feeler.
	 * @param map Handle to the map
	 * @return Which direction should it go, yo.
	 */
	public int getIntialDirection(Point feeler, Point wall, boolean goingRight, Tile[][] map) {
		return 0;
		/*
		 * The algorithm starts by looking at the direction between the feeler and its friend wall:
		 *  _
		 * | |
		 * | |
		 * |_|F    (so here we'd start by looking at direction = 6).
		 *   
		 * 
		 * It then cycles around the possible direction values until it finds an empty square.
		 * If we're going right, it goes around clockwise, if we're going left, it goes around
		 * counter-clockwise.
		 * 
		 * So here, looking for the direction the right feeler should go results in:
		 *  _
		 * | |
		 * |x|R    (we checked the spot where the "x" is and it came up a wall, so we kept going 'round
		 * |_|F     clockwise until we hit the spot where R is)
		 *   
		 * And left:
		 *  _
		 * | |
		 * | |
		 * |_|F    (we checked the direction counter-clockwise and immediately found an empty tile, so we
		 *  L       go in the direction of the L.)
		 *  
		 */

		//Start out by taking the direction between the wall and the feeler.
//		int diffX = wall.x - feeler.x;
//		int diffY = wall.y - feeler.y;
//		
//		
//		Point difference = new Point(diffX, diffY);

//		return getDirection(feeler, goingRight, map);
		//End result direction
//		int result = 0;
		
		

//		if(diffX == 0 && diffY == -1)  { result = 0; }
//		if(diffX == 1 && diffY == -1)  { result = 1; }
//		if(diffX == 1 && diffY == 0)   { result = 2; }
//		if(diffX == 1 && diffY == 1)   { result = 3; }
//		if(diffX == 0 && diffY == 1)   { result = 4; }
//		if(diffX == -1 && diffY == 1)  { result = 5; }
//		if(diffX == -1 && diffY == 0)  { result = 6; }
//		if(diffX == -1 && diffY == -1) { result = 7; }
//		
//		return getDirection(feeler, goingRight, map);
	}

	/**
	 * This method does the rest of the algorithm mentioned in getInitialDirection.
	 * 
	 * @param feeler 
	 * @param goingRight
	 * @param map
	 * @param diffX
	 * @param diffY
	 * @param result
	 * @return 
	 */
	private Point getDirection(Point feeler, boolean goingRight, Tile[][] map) {
		int diffX = 0, diffY = 0;
		
		//anti-infinity fail-safe
		int numTries = 0;
		while(numTries < 10) {
			//right feeler looks clockwise:
			if(goingRight) {
//				result = (result + 1) % 8;
//			} else {
//				result = (result - 1) % 8;
			}
			
			int result = 0;
			switch(result ) {
			case 0:
				diffX = 0; diffY = -1; 
				break;
			case 1:
				diffX = 1; diffY = -1; 
				break;
			case 2:
				diffX = 1; diffY = 0; 
				break;
			case 3:
				diffX = 1; diffY = 1; 
				break;
			case 4:
				diffX = 0; diffY = 1; 
				break;
			case 5:
				diffX = -1; diffY = 1; 
				break;
			case 6:
				diffX = -1; diffY = 0; 
				break;
			case 7:
				diffX = -1; diffY = -1; 
				break;
			}
			
			if(!map[feeler.x + diffX][feeler.y + diffY].blocker) {
				//We did it!
				return new Point(diffX, diffY);
			}
			
			numTries++;
		}
		
		//oops.
		throw new PANICEVERYTHINGISBROKENERROR();
	}

	private Point leftFeelerDirection;
	private Point rightFeelerDirection;

	/**
	 * Returns 
	 * 
	 * @param feelerX Current X of the feeler
	 * @param feelerY Current Y of the feeler
	 * @param goingRight Is the feeler supposed to follow the right wall, or left wall?
	 * @param feelerDirection Which direction did we last head cap'n?
	 * @param map A handle to the Tile map
	 * @return A Point where the X value is the dx the feeler should go, the Y value is the dy the feeler should go
	 */
	public Point followTheWall(Point feeler, boolean goingRight, int feelerDirection, Tile[][] map) {
		Point direction = getDirection(feeler, goingRight, map);
		return new Point(feeler.x + direction.x, feeler.y + direction.y);
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

		//Figure out the slope to the target
		//First, prevent /0 error:
		if(diffX == 0 && diffY < 0) {
			result.x = 0;
			result.y = -1;
			return result;
		}

		if(diffX == 0 && diffY > 0) {
			result.x = 0;
			result.y = 1;
			return result;
		}

		//Make sure it handles diffy = 0 correctly
		if(diffY == 0) {
			result.x = (int) Math.signum((float) diffX);
			result.y = 0;
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
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		//    /        ^ 
		//   /   or   /
		//  L        /
		if(-2 < slope && slope < -0.5) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 1 * (int) Math.signum((float) diffY);
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
	 * This takes them away.  It also checks if they're dead.
	 * 
	 * @param damage How much damage to do to the monster.
	 */
	public void getHit(int damage) {
		health -= damage;
		if(health <= 0) {
			die();

			if(whichEnemy == WIZARD) {
				//win
			}

			System.out.println("The " + name + " is slain!");
			
		}
	}
}
