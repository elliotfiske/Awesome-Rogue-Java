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
			double distance = Math.sqrt(p.x*p.x + p.y*p.y);
			if(distance < startDistance || distance > stopDistance) continue;
			
			//Choose a color
			int red = r.nextInt(stopColor.getRed() - startColor.getRed()) + startColor.getRed();
			int green = r.nextInt(stopColor.getGreen() - startColor.getGreen()) + startColor.getGreen();
			int blue = r.nextInt(stopColor.getBlue() - startColor.getBlue()) + startColor.getBlue();
			
			Color resultColor = new Color(red, green,blue);
			
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
		g2.fillRect( x * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_X - InGameState.CAMERA_X * InGameState.TILE_SIZE, 
				y * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_Y - InGameState.CAMERA_Y * InGameState.TILE_SIZE, 
				InGameState.TILE_SIZE, InGameState.TILE_SIZE);
	}
}
