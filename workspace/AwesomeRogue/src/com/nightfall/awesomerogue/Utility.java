package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/****
 * Has a bunch of handy static methods. I'm gonna move the pathfinding algorithm here in a bit.
 */
public class Utility {

	public static Random r = new Random();

	public static int sign(int i) {
		if(i < 0) return -1;
		if(i > 0) return 1;
		return 0;
	}

	/**
	 * Tells you which direction you should go based on a specified key. 
	 * @param e - KeyEvent with desired key.
	 * @return Point where x is the dx component and y is the dy component.
	 */
	public static Point getDirection(KeyEvent e) {
		Point result = new Point(0,0);

		switch(e.getKeyCode()) {
		case KeyEvent.VK_Y:
		case KeyEvent.VK_NUMPAD7:
			result = new Point(-1, -1);
			break;
		case KeyEvent.VK_U:
		case KeyEvent.VK_NUMPAD8:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_I:
		case KeyEvent.VK_NUMPAD9:
			result = new Point(1, -1);
			break;
		case KeyEvent.VK_H:
		case KeyEvent.VK_NUMPAD4:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_K:
		case KeyEvent.VK_NUMPAD6:
			result = new Point(1, 0);
			break;
		case KeyEvent.VK_N:
		case KeyEvent.VK_NUMPAD1:
			result = new Point(-1, 1);
			break;
		case KeyEvent.VK_M:
		case KeyEvent.VK_NUMPAD2:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_COMMA:
		case KeyEvent.VK_NUMPAD3:
			result = new Point(1, 1);
			break;

		case KeyEvent.VK_UP:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_LEFT:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_DOWN:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_RIGHT:
			result = new Point(1, 0);
			break;
		case KeyEvent.VK_SPACE:
		}

		return result;
	}

	/**
	 * Converts from a numbered direction style to a "difference" style direction.
	 * 
	 * @param numDirection Which direction you'd like converted to coordinates.
	 * @return A Point containing the two coordinates you had in mind.
	 */
	public static Point getPointDirection(int numDirection) {
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
	public static int getNumberedDirection(Point delta) {
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
			//throw new PANICEVERYTHINGISBROKENERROR("DiffX and DiffY are wrong! They're " + diffX + ", " + diffY);
		}

		return result;
	}

	/******************************************
	 *           PATHFINDING YO               *
	 ******************************************/

	/**
	 * Handy helper method.  Calculates the direction an enemy should logically take
	 * to walk STRAIGHT from (x, y) to (targetX, targetY).
	 * @param Point straightPoint The Point that is going to be modified to be further down the "straight" path
	 * @param Point targetPoint The Point that the straightPoint is moving towards.
	 * @param int smoothness Basically the "slope" that the path follows.  If 1, will always move diagonally if it can.
	 * @return Point with x from -1 --> 1 saying you should move that far in the x direction, 
	 * 		   and y from -1 --> 1 saying you should move that far in the y direction
	 */
	public static void walkStraight(Point straightPoint, Point targetPoint, double smoothness) {
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
	 * Given a start point and a finish point, tells you if there's a simple straight path there
	 * @param startPoint Where you start
	 * @param targetPoint Where you end
	 * @param smoothness See walkStraight
	 * @return True if there's a straight, unblocked path to the location, false otherwise.
	 */
	public static boolean straightPathExists(Point startPoint, Point targetPoint, double smoothness) {
		while(!(startPoint.x == targetPoint.x && startPoint.y == targetPoint.y)) {
			//Calculate which direction it would be smart to go in order to walk to the player.
			Utility.walkStraight(startPoint, targetPoint, smoothness);

			if(InGameState.map[startPoint.x][startPoint.y].blocker)
				return false;
		}

		return true;
	}

	/** This method converts from an int array that looks like:
	 *  [1,2,  3,4,  -1,9,  12,23] to a Point array with those same coordinates.
	 *  This is to make stuff easier to read in places like IceBlast and Explosion.
	 *  
	 *  You can pass in a "direction" to get the spray of ice/splosion to face a
	 *  different direction.
	 *  
	 *  Note that in general, you should pass in an "up" facing coords to modify with
	 *  "direction" (or a "up-right" facing coords)
	 *  
	 *  Also note that you'll need to have 2 different arrays, one that handles
	 *  cardinal directions and another that handles diagonals, since usually you
	 *  want them to look super different.
	 *  
	 * @param coords the array of ints
	 * @param direction Direction to modify the array to face.
	 * @return a sweet, sweet Point array.
	 */
	public static Point[] makePointArray(int[] coords, int direction) {
		//flip flop b/w xCoord and yCoord
		boolean swapXandY = false;

		//Directional modification coefficients
		int xCoeff = 1;
		int yCoeff = 1;

		//Create the result array that should be half the length of coords[]
		Point[] result = new Point[coords.length/2];

		//Initialize results with new points (-1037 is arbitrary, to raise eyebrows if it shows up.)
		for(int j = 0; j < result.length; j++) {
			result[j] = new Point(-1037, -1037);
		}

		switch(direction) {
		case 0:
			//Do nothing! You DID pass in an array representing the right direction, didn't you?
			break;
		case 1:
			//Do nothing! Same deal as case 0!
			break;
		case 2:
			swapXandY = true; //Facing this way -->
			xCoeff = -1;	  //Swap x and y, and make Y's negative
			break;
		case 3:
			yCoeff = -1; //  Facing this way \
			//Y's are negative   V
			break;
		case 4:
			yCoeff = -1; //Facing down: v
			//Y's are negative
			break;
		case 5:
			xCoeff = -1; //Facing this way    /
			yCoeff = -1; //X and Y negative  V
			break;
		case 6:
			swapXandY = true; //To the left now y'all <---
			//Swap X and Y
			break;
		case 7:
			xCoeff = -1; //Facing this way  ^
			//X's are negative  \ 
			break;
		default:
			throw new PANICEVERYTHINGISBROKENERROR("OH SNAP YOU PUT IN " + direction + 
					"INSTEAD OF A NORMAL DIRECTION, YOU WEIRDO");
		}

		//Put all the x coordinates in
		for(int i = 0; i < coords.length; i += 2) {
			if(swapXandY) {
				result[(int) i/2].y = coords[i] * yCoeff;
			} else {
				result[(int) i/2].x = coords[i] * xCoeff;
			}
		}

		//Put all the y coordinates in
		for(int i = 1; i < coords.length; i += 2) {
			if(swapXandY) {
				result[(int) i/2].x = coords[i] * xCoeff;
			} else {
				result[(int) i/2].y = coords[i] * yCoeff;
			}
		}
		return result;
	}

	public static Point[] makePointArray(int[] coords, Point direction) {
		return makePointArray(coords, getNumberedDirection(direction));
	}

	/**
	 * @return An ArrayList of points in a circle, suitable for an explosion
	 */
	public static Point[] makeCircularPointArray() {
		int MAX_RADIUS = 6;
		double ANGLE_STEP = Math.PI / 25;

		ArrayList<Point> arrayListResult = new ArrayList<Point>();

		for(int radius = 0; radius < MAX_RADIUS; radius++) {
			for(double angle = 0; angle < 2 * Math.PI; angle += ANGLE_STEP) {
				int x = (int) Math.round(radius * Math.cos(angle));
				int y = (int) Math.round(radius * Math.sin(angle));

				arrayListResult.add(new Point(x, y));
			}
		}

		//Convert from ArrayList to Array
		Point[] result = new Point[arrayListResult.size()];
		result = arrayListResult.toArray(result);

		System.out.println("booo");

		return result;
	}

	/**
	 * QUITE COMPLEX, NO? This gives you an array of ColorPoints (A color, at a point oooo) that correspond
	 * to the particle effect you generated with the following crazy arguments:
	 * @param x The origin X of the particle effect (where they all come from).
	 * @param y Origin Y.
	 * @param points The basic pattern of points that the particle effect follows. For instance, 
	 * in an explosion, this is just a circle around the origin.
	 * @param startDistance This is how far from the origin the particle effect STARTS.
	 * @param stopDistance This is how far from the origin the particle effect ENDS. You could make a sort of 
	 * "ring" around the player if you specified a high start distance and a high stop distance, for instance.
	 * @param startColor The darkest color a particle can be.
	 * @param stopColor The lightest color a particle can be. The method chooses a random color b/w start and 
	 * stop color for each particle.
	 * @param dispersion Your old friend, dispersion, with a NEW TWIST: now, each particle has a dispersion/100
	 * chance of being displayed. Pretty slick.
	 * @return Draw each color, at each point, to make the particle effect happen on-screen.
	 */
	public static ArrayList<ColorPoint> particleEffect(Point[] points, 
			double startDistance, double stopDistance, Color startColor, Color stopColor, int dispersion) {
		ArrayList<ColorPoint> result = new ArrayList<ColorPoint>();

		for(Point p: points) {
			//First, see if dispersion even lets us show a point here
			if(Math.random() * 100 < dispersion) continue;

			//Next, determine if the distance lets us show a point here
			double distance = Math.floor(Math.sqrt(p.x*p.x + p.y*p.y));
			if(distance < startDistance || distance > stopDistance) continue;

			//Choose a color randomly within the correct range
			int red =   r.nextInt(stopColor.getRed()   - startColor.getRed())   + startColor.getRed();
			int green = r.nextInt(stopColor.getGreen() - startColor.getGreen()) + startColor.getGreen();
			int blue =  r.nextInt(stopColor.getBlue()  - startColor.getBlue())  + startColor.getBlue();
			int alpha = r.nextInt(stopColor.getAlpha() - startColor.getAlpha()) + startColor.getAlpha();

			Color resultColor = new Color(red, green, blue, alpha);

			//Make a new ColorPoint and add it to the result
			result.add(new ColorPoint(resultColor, p));
		}

		return result;
	}

	/**
	 * Draw over the specified tile with the specified color.
	 * @param x
	 * @param y
	 * @param g2
	 */
	public static void drawTileRectangle(int x, int y, Color color, Graphics2D g2) {
		g2.setColor(color);
		g2.fillRect( x * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_X - InGameState.CAMERA_PX_X, 
				y * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_Y - InGameState.SHAKEN_CAMERA_Y, 
				InGameState.TILE_SIZE, InGameState.TILE_SIZE);
	}

	/**********************************************************************/
	/* 							PATHFINDING YO						      */
	/**********************************************************************/
	
	/** Does as the method name suggests. */
	public static void pathToPointAndMove(Character mover, int targetX, int targetY) {
		//Grab a handle to the entities array and the map
		Character[][] entities = InGameState.getEntities();
		Tile[][] map = InGameState.map;
		
		int x = mover.x;
		int y = mover.y;
		
		//Try to path straight from the current position to the target.

		//Coordinates of the line that walks to the player.
		Point straightPoint = new Point(x, y);

		/** List of tiles straight from the monster to the player. */
		ArrayList<Tile> straightTiles = new ArrayList<Tile>();

		while(!(straightPoint.x == targetX && straightPoint.y == targetY)) {
			//Calculate which direction it would be smart to go in order to walk to the player.
			Utility.walkStraight(straightPoint, new Point(targetX, targetY), 3);

			straightTiles.add(new Tile( map[straightPoint.x][straightPoint.y].type , 0, straightPoint.x, straightPoint.y));

			if(map[straightPoint.x][straightPoint.y].isNotEmpty()) {
				//map[straightPoint.x][straightPoint.y].illustrate(Color.red);//TODO
				//if(entities[straightPoint.x][straightPoint.y] != null && entities[straightPoint.x][straightPoint.y].getName() == "Main Character")
				//map[straightPoint.x][straightPoint.y].illustrate(Color.green);
			} else {
				//map[straightPoint.x][straightPoint.y].illustrate(Color.yellow);// TODO
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

			//Go through until we run into sexy trouble (blocker) or another enemy.
			//Also make sure that the tile PREVIOUS to this one is NOT a blocker (so we don't do two blockers in a row).
			if((t.isNotEmpty() && !prevTile.blocker)) {
				//OH NO! Blocker found.  Send out "feelers" to go along right and left walls.
				//Start feelers at the square on the straight-line path right BEFORE the wall.
				Point rightFeeler = new Point(prevTile.x, prevTile.y);
				Point leftFeeler = new Point(prevTile.x, prevTile.y);
				ArrayList<Point> leftPath = new ArrayList<Point>();
				ArrayList<Point> rightPath = new ArrayList<Point>();
				leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
				rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

				//Get started on feelin' things out.
				//Right:
				mover.lastWallRight = getDirection(rightFeeler, new Point(t.x, t.y), true);

				//Left:
				mover.lastWallLeft = getDirection(leftFeeler, new Point(t.x, t.y), false);

				if(mover.lastWallRight.equals(new Point(0,0)) || mover.lastWallLeft.equals(new Point(0,0))) {
					return;
				}

				Point lastRightFeeler = new Point(rightFeeler.x, rightFeeler.y);
				Point lastLeftFeeler = new Point(leftFeeler.x, leftFeeler.y);

				leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
				rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

				//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO
				//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

				int numTiles = 0;
				while(numTiles < 1000) {
					//follow right wall

					mover.lastWallRight = getDirection(rightFeeler, mover.lastWallRight, true);

					//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO

					//follow left wall
					mover.lastWallLeft = getDirection(leftFeeler, mover.lastWallLeft, false);

					if(mover.lastWallRight.equals(new Point(0,0)) || mover.lastWallLeft.equals(new Point(0,0))) {
						return;
					}

					//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

					leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
					rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

					//See if the PREVIOUS feeler and the CURRENT feeler cross over the straight tiles path.
					Point intersection = findIntersection(straightTiles, rightFeeler, lastRightFeeler, whichTile);
					if(!intersection.equals(new Point(-1, -1))) {
						if(firstCorrectPath == null) {
							firstCorrectPath = rightPath;
						}
						break;
					}

					intersection = findIntersection(straightTiles, leftFeeler, lastLeftFeeler, whichTile);
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

				if(numTiles >= 999) {
					//No path found, I guess.
					break;
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
				//map[pointToCheck.x][pointToCheck.y].illustrate(Color.pink); //TODO

				//If there's a straight, unblocked path to the pink tile we've just found our
				//route to the player.
				Point finalPath = new Point(x, y);

				ArrayList<Point> finalPathPoints = new ArrayList<Point>();
				finalPathPoints.add(new Point(finalPath.x, finalPath.y));

				/** The step we WOULD take to follow this new path is: */
				Point firstStep = new Point(finalPath.x, finalPath.y);
				Utility.walkStraight(firstStep, new Point(pointToCheck.x, pointToCheck.y), 3);

				//optimism!
				weDidIt = true;

				while(finalPath.x != pointToCheck.x || finalPath.y != pointToCheck.y) {
					Utility.walkStraight(finalPath, pointToCheck, 3);
					finalPathPoints.add(new Point(finalPath.x, finalPath.y));
					//map[finalPath.x][finalPath.y].illustrate(Color.black); //TODO
					if(map[finalPath.x][finalPath.y].isNotEmpty()) {
						//map[finalPath.x][finalPath.y].illustrate(Color.red); //TODO
						//Outta luck.  Try the next one!
						weDidIt = false;
						break;
					}
				}

				//straight path found! rejoice!
				if(weDidIt) {
					proposedDX = firstStep.x - x;
					proposedDY = firstStep.y - y;
					//for(Point p : finalPathPoints) {
						//map[p.x][p.y].illustrate(Color.green); //TODO
					//}
					//map[firstStep.x][firstStep.y].illustrate(Color.ORANGE); //TODO
					break;
				}
			}

			//Don't worry.  Just follow the feelers from before.
			if(!weDidIt) {

			}
		} else {
			//potential problem if the rat is inside of me
			if(straightTiles.size() == 0) {
				throw new PANICEVERYTHINGISBROKENERROR("THE " + mover.getName() + " IS INSIDE ME! INSIIIIIIDE ME!!!");
			}

			//There must have been no obstacles.  Follow the straight path.
			proposedDX = straightTiles.get(0).x - x;
			proposedDY = straightTiles.get(0).y - y;
			//map[x + proposedDX][y + proposedDY].illustrate(Color.ORANGE);
		}

		if(entities[x + proposedDX][y + proposedDY] instanceof MainCharacter) {
			System.out.println("The rat scratches you!");
			entities[x + proposedDX][y + proposedDY].getHit(5, null, null);
		} else if(map[x + proposedDX][y + proposedDY].isNotEmpty()) {
			//We pathed into a wall.  Oh well.  Don't move!
		} else {
			mover.moveTo(x + proposedDX, y + proposedDY);
		}
	}

	public static Point getDirection(Point feeler, Point lastWall, boolean goingRight) {
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

		int result = Utility.getNumberedDirection(new Point(diffX, diffY));
		if(result == -1) {
			//We're probably stuck in a crowd. Just chill.
			System.out.println("The guy at " + feeler.x + ", " + feeler.y + "doesn't like you.  Zooming in now:");

			return new Point(0,0);
		}

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

			diffX = Utility.getPointDirection(result).x;
			diffY = Utility.getPointDirection(result).y;

			//Outta bounds check!
			if(feeler.x + diffX < 0 || feeler.x + diffX >= InGameState.map.length ||
					feeler.y + diffY < 0 || feeler.y + diffY >= InGameState.map[0].length) {
				continue; //pretend it's a blocker.
			}

			if(!InGameState.map[feeler.x + diffX][feeler.y + diffY].isNotEmpty()) {
				//We did it!
				//Grab the result of this function: the last-touched wall.
				int wallDirection = Utility.getNumberedDirection(new Point(diffX, diffY));

				//(it should be one cycle back).
				if(goingRight) {
					wallDirection = (wallDirection - 1);
					if(wallDirection < 0) {
						wallDirection = 7;
					}
				} else {
					wallDirection = (wallDirection + 1) % 8;
				}

				Point wallDiff = Utility.getPointDirection(wallDirection);
				Point lastWallTouched = new Point(feeler.x + wallDiff.x, feeler.y + wallDiff.y);

				//Move the feeler to the proper location:
				feeler.x += diffX;
				feeler.y += diffY;

				return lastWallTouched;
			}

			numTries++;
		}

		//We're probably stuck in a crowd. Just chill.
		System.out.println("The guy at " + feeler.x + ", " + feeler.y + "doesn't like you.  Zooming in now:");

		return new Point(0,0);
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
	public static Point findIntersection(ArrayList<Tile> straightTiles,
			Point feeler, Point lastFeeler, int whichTile) {

		//3 points to check:
		// xp
		// fx
		//
		//"f", and both "x"s.

		Tile[][] map = InGameState.map;
		
		//check the "f"
		if(straightTiles.indexOf(map[feeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][feeler.y]) != -1) {
			return new Point(feeler.x, feeler.y);
		}

		//check one "x"
		if(straightTiles.indexOf(map[feeler.x][lastFeeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][lastFeeler.y]) != -1) {
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		//check other "x"
		if(straightTiles.indexOf(map[lastFeeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[lastFeeler.x][lastFeeler.y]) != -1) {
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		return new Point(-1, -1);
	}

	/**
	 * Move randomly. Rats occasionally do this, and maybe we'll have a "confused" status!
	 * @param map I'm the map, I'm the map, I'm the map, I'm the map, I'M THE MAP
	 */
	public static void moveRandomly(Character mover) {
		int randDirection = (int) Math.floor(Math.random() * 8);
		Point randPoint = Utility.getPointDirection(randDirection);
		int numTries = 0;
		
		while(InGameState.map[mover.x + randPoint.x][mover.y + randPoint.y].isNotEmpty() && numTries < 12) {
			randDirection = (int) Math.floor(Math.random() * 8);
			randPoint = Utility.getPointDirection(randDirection);
			
			numTries++;
		}
		
		if(numTries < 12) {
			mover.moveTo(mover.x + randPoint.x, mover.y + randPoint.y);
		}
	}

	/**********************************************************************/
	/* 			              END PATHFINDING YO                          */
	/**********************************************************************/
	
}
