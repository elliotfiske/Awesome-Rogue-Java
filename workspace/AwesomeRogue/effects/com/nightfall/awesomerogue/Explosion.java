package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Explosion extends Effect {

	private int x, y;
	private int iterations;
	private static final int MAX_ITERATIONS = 8;
	
	private static final long cooldown = 32;
	private long elapsedTime;
	
	//Generate a set of Points that are in a circle.
	private static Point[] points = Utility.makeCircularPointArray();
	
	private static final double[] START_DISTANCES = 
		{0, 0, 0, 1, 2, 3, 4, 5, 6};
	private static final double[] STOP_DISTANCES  =  
		{1, 2, 3, 4, 5, 6, 7, 8, 8};
	
	private ArrayList<ColorPoint> colorPoints;
	
	private static Color startColor = new Color(212, 25, 25, 200);
	private static Color stopColor = new Color(255, 239, 69, 255);
	
	public Explosion(int x, int y) {
		super("Explosion");
		this.x = x;
		this.y = y;
		iterations = 0;
		elapsedTime = 0;
		colorPoints = new ArrayList<ColorPoint>();
		
		InGameState.shakeScreen(10);
	}

	@Override
	public void iterate(long deltaTime) {
		//Check the cooldown
		elapsedTime += deltaTime;
		if(elapsedTime < cooldown) {
			return;
		}
		elapsedTime = 0;
		
		colorPoints = Utility.particleEffect(points, START_DISTANCES[iterations], STOP_DISTANCES[iterations],
				startColor, stopColor, 65);
		
		for(int p = 0; p < colorPoints.size(); p++) {
			int cx = colorPoints.get(p).point.x + x;
			int cy = colorPoints.get(p).point.y + y;
			
			//Check if there's anything blocking the explosion
			if(!(Utility.straightPathExists(new Point(x, y), new Point(cx, cy), 1) || 
					Utility.straightPathExists(new Point(x, y), new Point(cx, cy), 10))) {
				colorPoints.remove(p);
				p--;
				continue;
			}
			
			//If you can't see the tile, darken the explosion there
			if(!InGameState.map[cx][cy].visible) {
				colorPoints.get(p).color = colorPoints.get(p).color.darker().darker().darker();
			}
		}
		
		if(iterations >= MAX_ITERATIONS) {
			setRunning(false);
		}
		iterations++;
	}

	@Override
	public void render(Graphics2D g2) {
		//Draw each ColorPoint
		for(ColorPoint cp : colorPoints) {
			//Cell coordinates of current freezysquare
			int cx = cp.point.x + x;
			int cy = cp.point.y + y;
			
			Utility.drawTileRectangle(cx, cy, cp.color, g2);
		}
	}

	@Override
	public void reverse() {

	}

}
