package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class IceBlast extends Effect {

	int x, y;
	Point direction;
	int iterations;
	
	//The basic pattern of tiles that are affected by an ice blast
	private static final int[] iceBlastStraightCoords = 
			{-2,-8, -2,-7, -2,-6, -2,-5,
		     -1,-8, -1,-7, -1,-6, -1,-5, -1,-4, -1,-3, -1,-2,
		      0,-1,  0,-2,  0,-3,  0,-4,  0,-5,  0,-6,  0,-7, 0,-8,
		      1,-8,  1,-7,  1,-6,  1,-5,  1,-4,  1,-3,  1,-2,
		      2,-8,  2,-7,  2,-6,  2,-5                         };
	
	//The pattern of tiles is different when you fire it diagonally
	private static final int[] iceBlastDiagonalCoords = 
			{1,-1, 1,-2, 2,-1, 2,-2, 2,-3, 2,-4, 3,-2, 3,-3, 3,-4, 3,-5, 4,-2, 4,-3, 4,-4, 4,-5, 4,-6, 4,-7, 4,-8,
			 5,-3, 5,-4, 5,-5, 5,-6, 5,-7, 6,-4, 6,-5, 6,-6, 7,-4, 7,-5, 8,-4};
	
	//Start distance defines how far away from yo body the ice STARTS rendering
	private static final double[] startDistances = 
		{0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 9.5, 9.8, 10};
	//Stop distance defines how far away from ya bod the ice STOPS rendering
	private static final double[] stopDistances  =  
		{1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 9, 9.8, 10, 10};
	
	private static Color startColor = new Color(34, 143, 186, 50);
	private static Color stopColor = new Color(138, 222, 255, 255);
	
	public IceBlast(int x, int y, Point direction) {
		super("Ice Blast");
		this.x = x;
		this.y = y;
		this.direction = direction;
		iterations = 0;
	}

	@Override
	public void renderAndIterate(Graphics2D g2) {
		Point[] points;
		int numDirection = Utility.getNumberedDirection(direction);
		if(numDirection % 2 == 0) { //Even directions are cardinal
			points = Utility.makePointArray(iceBlastStraightCoords, numDirection);
		} else { //Odd directions are diagonal
			points = Utility.makePointArray(iceBlastDiagonalCoords, numDirection);
		}
		ArrayList<ColorPoint> colorPoints = Utility.particleEffect(
				points, startDistances[iterations], stopDistances[iterations], 
				startColor, stopColor, 0);
		
		//Draw each ColorPoint we got back
		for(ColorPoint cp : colorPoints) {
			//Cell coordinates of current freezysquare
			int cx = cp.point.x + x;
			int cy = cp.point.y + y;
			
			//Check if there's anything blocking the freeze blast
			if(!Utility.straightPathTo(new Point(x, y), new Point(cx, cy), 10)) {
				continue;
			}
			
			//See if there are monsters to freeze!
			Character victim = InGameState.getEntities()[cx][cy];
			if(victim != null && victim instanceof Enemy) {
				((Enemy) victim).frozen = 10;
			}
			
			Utility.drawTileRectangle(cp.point.x + x, cp.point.y + y, cp.color, g2);
		}
		
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		iterations++;
		if(iterations >= startDistances.length - 1) {
			setRunning(false);
		}
	}

	@Override
	public void render(Graphics2D g2) {
		//Do nothing?? Why is this even here???!?
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub

	}

}
