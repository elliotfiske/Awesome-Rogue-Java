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

	private int xBounty, yBounty;
	private Tile bounty;

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

	public void setBounty(int x, int y, Tile bountyTile) {
		xBounty = x;
		yBounty = y;
		bounty = bountyTile;
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
	/**
	 * @param targetX
	 * @param targetY
	 * @param map
	 */
	public void pathToHeroAndMove(int targetX, int targetY, Tile[][] map) {
		//Try to path straight from the monster to the hero.

		//Coordinates of the line that walks to the player.
		Point straightPoint = new Point(x, y);

		/** List of tiles straight from the monster to the player. */
		ArrayList<Tile> straightTiles = new ArrayList<Tile>();

		while(!(straightPoint.x == targetX && straightPoint.y == targetY)) {
			//Calculate which direction it would be smart to go in order to walk to the player.
			walkStraight(straightPoint, new Point(targetX, targetY), 3);
			
			straightTiles.add(new Tile( map[straightPoint.x][straightPoint.y].type , 0, straightPoint.x, straightPoint.y));

			if(map[straightPoint.x][straightPoint.y].blocker) {
				map[straightPoint.x][straightPoint.y].illustrate(Color.red);//TODO
			} else {
				map[straightPoint.x][straightPoint.y].illustrate(Color.yellow);// TODO
			}
		}

		//DEAL WITH OBSTACLES HERE

		/** Save the FIRST list of tiles we get.  This is because the enemy is looking to address the FIRST obstacle in his way,
		 * and only really cares that there does exist a path to you at the end. */
		ArrayList<Point> firstCorrectPath = null;

		for(int whichTile = 0; whichTile < straightTiles.size(); whichTile++) {
			Tile t = straightTiles.get(whichTile);
			Tile prevTile = null;
			
			//Ran into some nasty array index out of bounds exceptions don'cha know.
			if(whichTile == 0) {
				prevTile = map[x][y];
			} else {
				prevTile = straightTiles.get(whichTile - 1);
			}
			
			//Go through until we run into sexy trouble (blocker)
			//Also make sure that the tile PREVIOUS to this one is NOT a blocker (so we don't do two blockers in a row).
			if(t.blocker && !prevTile.blocker) {
				//OH NO! Blocker found.  Send out "feelers" to go along right and left walls.
				//Start feelers at the square on the straight-line path right BEFORE the wall.
				Point rightFeeler = null;
				Point leftFeeler = null;
				ArrayList<Point> leftPath = new ArrayList<Point>();
				ArrayList<Point> rightPath = new ArrayList<Point>();
				if(whichTile == 0) {
					//If the very first tile looked at was a blocker, use the enemy coordinates.
					rightFeeler = new Point(x, y);
					leftFeeler = new Point(x, y);
					leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
					rightPath.add(new Point(rightFeeler.x, rightFeeler.y));
				} else {
					rightFeeler = new Point(straightTiles.get(whichTile - 1).x, straightTiles.get(whichTile - 1).y);
					leftFeeler = new Point(rightFeeler.x, rightFeeler.y);
					leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
					rightPath.add(new Point(rightFeeler.x, rightFeeler.y));
				}

				//Get started on feelin' things out.
				//Right:
				lastWallRight = getDirection(rightFeeler, new Point(t.x, t.y), true, map);

				//Left:
				lastWallLeft = getDirection(leftFeeler, new Point(t.x, t.y), false, map);

				Point lastRightFeeler = new Point(rightFeeler.x, rightFeeler.y);
				Point lastLeftFeeler = new Point(leftFeeler.x, leftFeeler.y);

				leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
				rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

				//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO
				//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

				int numTiles = 0;
				while(numTiles < 100) {
					//follow right wall

					lastWallRight = getDirection(rightFeeler, lastWallRight, true, map);

					//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO

					//follow left wall
					lastWallLeft = getDirection(leftFeeler, lastWallLeft, false, map);

					//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

					leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
					rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

					//See if the PREVIOUS feeler and the CURRENT feeler cross over the straight tiles path.
					Point intersection = findIntersection(straightTiles, rightFeeler, lastRightFeeler, whichTile, map);
					if(!intersection.equals(new Point(-1, -1))) {
						if(firstCorrectPath == null) {
							firstCorrectPath = rightPath;
						}
						break;
					}

					intersection = findIntersection(straightTiles, leftFeeler, lastLeftFeeler, whichTile, map);
					if(!intersection.equals(new Point(-1, -1))) {
						if(firstCorrectPath == null) {
							firstCorrectPath = leftPath;
						}
						break;
					}

					//Store the previous feeler coordinates to make sure we don't accidentally let something slip
					//diagonally through.
					lastRightFeeler = new Point(rightFeeler.x, rightFeeler.y);
					lastLeftFeeler = new Point(leftFeeler.x, leftFeeler.y);

					numTiles++;
				}
			}
		}

		//The LAST AND FINAL thing this algorithm does is look for the best way to follow the twisty path we've just
		//laid out for the enemy.
		int proposedDX = 0;
		int proposedDY = 0;
		if(firstCorrectPath != null) {
			
			boolean weDidIt = false;
			for(int i = firstCorrectPath.size() - 1; i > 0; i--) {
				Point pointToCheck = firstCorrectPath.get(i);
				map[pointToCheck.x][pointToCheck.y].illustrate(Color.pink);
				//If there's a straight, unblocked path to the pink tile we've just found our
				//route to the player.
				Point finalPath = new Point(x, y);
				
				/** The step we WOULD take to follow this new path is: */
				Point firstStep = new Point(finalPath.x, finalPath.y);
				walkStraight(firstStep, new Point(targetX, targetY), 1);
				
				//optimism!
				weDidIt = true;
				while(finalPath.x != pointToCheck.x && finalPath.y != pointToCheck.y) {
					walkStraight(finalPath, pointToCheck, 1);
					//map[finalPath.x][finalPath.y].illustrate(Color.black); //TODO
					if(map[finalPath.x][finalPath.y].blocker) {
						//map[finalPath.x][finalPath.y].illustrate(Color.red); //TODO
						//Outta luck.  Try the next one!
						weDidIt = false;
						break;
					}
				}
				
				//straight path found! rejoice!
				if(weDidIt) {
					System.out.println("did it lol");
					proposedDX = firstStep.x - x;
					proposedDY = firstStep.y - y;
					//map[firstStep.x][firstStep.y].illustrate(Color.ORANGE);
					break;
				}
			}
			
			//Don't worry.  Just follow the feelers from before.
			if(!weDidIt) {
				
			}
		} else {
			//There must have been no obstacles.  Follow the straight path.
			proposedDX = straightTiles.get(0).x - x;
			proposedDY = straightTiles.get(0).y - y;
			//map[x + proposedDX][y + proposedDY].illustrate(Color.ORANGE);
		}
		
		x += proposedDX;
		y += proposedDY;
	}	

	/**
	 * This method looks for an intersection between the straightTiles path and the feeler path.
	 * @param straightTiles The list of tiles that lead straight to the player.
	 * @param rightFeeler The "feeler" that was sent out along the right wall.
	 * @param lastRightFeeler The "feeler" that was sent out along the left wall.
	 * @param whichTile The spot in the straightTiles ArrayList that we know will move us forward.
	 * @param map A handle to the map.
	 * @return The Point they intersect, or (-1, -1) if they don't.
	 */
	public Point findIntersection(ArrayList<Tile> straightTiles,
			Point feeler, Point lastFeeler, int whichTile, Tile[][] map) {

		//3 points to check:
		// xp
		// fx
		//
		//"f", and both "x"s.

		//check the "f"
		if(straightTiles.indexOf(map[feeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][feeler.y]) != -1) {
			map[feeler.x][feeler.y].illustrate(Color.green);
			return new Point(feeler.x, feeler.y);
		}

		//check one "x"
		if(straightTiles.indexOf(map[feeler.x][lastFeeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][lastFeeler.y]) != -1) {
			map[feeler.x][lastFeeler.y].illustrate(Color.green);
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		//check other "x"
		if(straightTiles.indexOf(map[lastFeeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[lastFeeler.x][lastFeeler.y]) != -1) {
			map[lastFeeler.x][feeler.y].illustrate(Color.green);
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		return new Point(-1, -1);
	}

	/**
	 * Converts from point with directional components --> one number representin direction.
	 * 
	 * Directions:
	 * 7 0 1
	 * 6 X 2
	 * 5 4 3
	 * 
	 * @param delta Point with |x| <= 1 and |y| <= 1 describing direction
	 * @return Sweet, sweet directional number.
	 * @throws PANICEVERYTHINGISBROKENERROR OH NO WHAT HAVE YOU DONE OH NOOOOO
	 */
	public int getNumberedDirection(Point delta) {
		int diffX = delta.x;
		int diffY = delta.y;
		int result = -1;

		if(diffX == 0 && diffY == -1)  { result = 0; }
		if(diffX == 1 && diffY == -1)  { result = 1; }
		if(diffX == 1 && diffY == 0)   { result = 2; }
		if(diffX == 1 && diffY == 1)   { result = 3; }
		if(diffX == 0 && diffY == 1)   { result = 4; }
		if(diffX == -1 && diffY == 1)  { result = 5; }
		if(diffX == -1 && diffY == 0)  { result = 6; }
		if(diffX == -1 && diffY == -1) { result = 7; }

		if(result == -1) {
			throw new PANICEVERYTHINGISBROKENERROR("DiffX and DiffY are wrong! They're " + diffX + ", " + diffY);
		}

		return result;
	}

	/**
	 * This method takes a feeler and makes it follow the right wall.
	 * 
	 * @param feeler The feeler that will be moved along the wall.
	 * @param lastWall The last wall that the feeler touched.
	 * @param goingRight True if we're following the right wall, false otherwise.
	 * @param map The array of Tiles.
	 * @return The last WALL the feeler touched. This is important to the wall-following algorithm.
	 */
	public Point getDirection(Point feeler, Point lastWall, boolean goingRight, Tile[][] map) {
		/*
		 * The algorithm starts by looking at the direction between the feeler and its last-touched wall:
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
		int diffX = 0, diffY = 0;

		diffX = lastWall.x - feeler.x;
		diffY = lastWall.y - feeler.y;

		int result = getNumberedDirection(new Point(diffX, diffY));

		//anti-infinity fail-safe
		int numTries = 0;
		while(numTries < 10) {
			//right feeler looks clockwise:
			if(goingRight) {
				result = (result + 1) % 8;
			} else {
				result = (result - 1);
				if(result < 0) {
					result = 7;
				}
			}

			diffX = getPointDirection(result).x;
			diffY = getPointDirection(result).y;

			//Outta bounds check!
			if(feeler.x + diffX < 0 || feeler.x + diffX >= map.length ||
					feeler.y + diffY < 0 || feeler.y + diffY >= map[0].length) {
				continue; //pretend it's a blocker.
			}

			if(!map[feeler.x + diffX][feeler.y + diffY].blocker) {
				//We did it!
				//Grab the result of this function: the last-touched wall.
				int wallDirection = getNumberedDirection(new Point(diffX, diffY));

				//(it should be one cycle back).
				if(goingRight) {
					wallDirection = (wallDirection - 1);
					if(wallDirection < 0) {
						wallDirection = 7;
					}
				} else {
					wallDirection = (wallDirection + 1) % 8;
				}

				Point wallDiff = getPointDirection(wallDirection);
				Point lastWallTouched = new Point(feeler.x + wallDiff.x, feeler.y + wallDiff.y);

				//Move the feeler to the proper location:
				feeler.x += diffX;
				feeler.y += diffY;

				return lastWallTouched;
			}

			numTries++;
		}

		//oops.
		throw new PANICEVERYTHINGISBROKENERROR("We couldn't find the next Tile for the feeler to move to :(");
	}

	/**
	 * Converts from a numbered direction style to a "difference" style direction.
	 * 
	 * @param numDirection Which direction you'd like converted to coordinates.
	 * @return A Point containing the two coordinates you had in mind.
	 */
	private Point getPointDirection(int numDirection) {
		int diffX = 0, diffY = 0;

		switch(numDirection) {
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

		if(diffX == 0 && diffY == 0) {
			throw new PANICEVERYTHINGISBROKENERROR("Oh man.  You put in a direction number outside of 0-7 you dolt.  Entered value: " + numDirection);
		}
		return new Point(diffX, diffY);

	}

	private Point lastWallLeft;
	private Point lastWallRight;

	/**
	 * Handy helper method.  Calculates the direction an enemy should logically take
	 * to walk STRAIGHT from (x, y) to (targetX, targetY).
	 * @param Point straightPoint The Point that is going to be modified to be further down the "straight" path
	 * @param Point targetPoint The Point that the straightPoint is moving towards.
	 * @param int smoothness Basically the "slope" that the path follows.  If 1, will always move diagonally if it can.
	 * @return Point with x from -1 --> 1 saying you should move that far in the x direction, 
	 * 		   and y from -1 --> 1 saying you should move that far in the y direction
	 */
	public void walkStraight(Point straightPoint, Point targetPoint, int smoothness) {
		Point result = new Point(-100,-100);

		int x = straightPoint.x;
		int y = straightPoint.y;
		
		int targetX = targetPoint.x;
		int targetY = targetPoint.y;
		
		int diffX = targetX - x;
		int diffY = targetY - y;

		//Figure out the slope to the target
		//First, prevent /0 error:
		if(diffX == 0 && diffY < 0) {
			straightPoint.x +=  0;
			straightPoint.y +=  -1;
			return;
		}

		if(diffX == 0 && diffY > 0) {
			straightPoint.x +=  0;
			straightPoint.y +=  1;
			return;
		}

		//Make sure it handles diffy = 0 correctly
		if(diffY == 0) {
			straightPoint.x += (int) Math.signum((float) diffX);
			straightPoint.y +=  0;
			return;
		}

		double slope = (double) diffY / diffX;

		//I hand-calculated these values, on some sweet graph paper.
		//Come look at it sometime.

		//  |      ^
		//  |  or  |
		//  v      |
		if(slope <= -smoothness) {
			result.x = 0;
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		//    /        ^ 
		//   /   or   /
		//  L        /
		if(-smoothness < slope && slope < -1/smoothness) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		//
		// < - -   or  - - >
		//
		if(-1/smoothness <= slope && slope <= 1/smoothness) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 0;
		}

		// ^       \
		//  \  or   \
		//   \       V
		if(1/smoothness < slope && slope < smoothness) {
			result.x = 1 * (int) Math.signum((float) diffX);
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		//  |      ^
		//  |  or  |
		//  v      |
		if(slope >= smoothness) {
			result.x = 0;
			result.y = 1 * (int) Math.signum((float) diffY);
		}

		straightPoint.x += result.x;
		straightPoint.y += result.y;
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
	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		health -= damage;
		if(health <= 0) {
			die();
			entities[x][y] = null;

			if(bounty != null) {
				map[xBounty][yBounty] = bounty;
			}

			if(whichEnemy == WIZARD) {
				//win
			}

			System.out.println("The " + name + " is slain!");

		}
	}
}
