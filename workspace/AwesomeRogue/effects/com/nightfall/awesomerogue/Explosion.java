package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class Explosion extends Effect {
	// Dispersion. Math.random()*100 < dispersion means that there will be no explosion
	// At that point. Small number = tight, large number = few flames
	public static final int[] DISPERSION = new int[] {0,0,0,0,0,20,20,20,20,30,30,30,35,45,60,75,90,90}; 
	// Size od the explosion
	public static final int[] SIZES = new int[] 	 {1,1,2,2,2, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 5, 5};//{1,1,2,2, 4, 4, 4, 6}; 
	
	// Keeps track of its position and size
	private double x, y;
	private int sizeindex;
	private Color[][] explosionSquares;
	
	private double directionAngle;
	private Double direction;
	
	public Explosion(int x, int y) {
		super("Explosion Effect");
		this.x = x - InGameState.CAMERA_X;
		this.y = y - InGameState.CAMERA_Y;
		sizeindex = 0;
		int size = SIZES[sizeindex];
		
		// Initialize array of no colors
		explosionSquares = new Color[size][size];
	}
	
	public Explosion(int x, int y, Point direction) {
		this(x, y);
		
		// Compute the angle we want it to be directed in
		if(direction.x == 0) {
			this.directionAngle = Math.PI / 2 * ((direction.y > 0) ? 1 : 3);
		}
		else {
			this.directionAngle = Math.atan(direction.y / direction.x);
			
			// Because the way arctan works, it doesn't take the second half of the circle into account
			// Ergo gotta check that
			if(direction.x < 0) directionAngle += Math.PI;
		}
		System.out.println(directionAngle/Math.PI);
		
		this.direction = new Point2D.Double((double)direction.x / SIZES.length, (double)direction.y / SIZES.length);
	}

	public void renderAndIterate(Graphics2D g2, Tile[][] map, Character[][] entities) {
		if(!running()) return;
		render(g2, map);
		iterateExplosion(map, entities);
		
	}
	
	public void render(Graphics2D g2) {
		// Nothing really to do without map knowledge
	}
	
	public void render(Graphics2D g2, Tile[][] map) {
		int half = explosionSquares.length / 2;
		for(int i = 0; i < explosionSquares.length; i ++) {
			for(int j = 0; j < explosionSquares[0].length; j ++) {
				// Don't try to draw if there's no color there!
				if(explosionSquares[i][j] == null) continue;
				// Or if you can't see that map tile
				int x = (int) this.x + i-half;
				int y = (int) this.y + j-half;
				
				if(x < 0 || y < 0 || x >= map.length || y >= map[0].length) continue;
				if(!map[x][y].visible || map[x][y].blocker) continue;
				
				// Otherwise change color and draw a rectangle
				g2.setColor(explosionSquares[i][j]);
				g2.fillRect(x*InGameState.TILE_SIZE+InGameState.INGAME_WINDOW_OFFSET_X, 
						y*InGameState.TILE_SIZE+InGameState.INGAME_WINDOW_OFFSET_Y, 
						InGameState.TILE_SIZE, InGameState.TILE_SIZE);
			}
		}
	}
	
	private void iterateExplosion(Tile[][] map, Character[][] entities) {
		if(sizeindex >= SIZES.length) {	// IF we reach the end of the explosion, stop exploding
			explosionSquares = new Color[0][0];
			setRunning(false);
			return;
		}
		if(direction != null) {
			x += direction.x;
			y += direction.y;
		}
		
		int size = SIZES[sizeindex]; 
		int arrsize = size * 2;
		int dispersion = DISPERSION[sizeindex];
		explosionSquares = new Color[arrsize][arrsize];
		for(int radius = 0; radius < size; radius ++) {
			for(int i = 0; i < arrsize*2; i ++) {
				if(Math.random()*100 < dispersion) continue;	// Take dispersion into account
				
				// Make a reddish orange color
				int r = (int) (Math.random()*105 + 150);
				int g = (int) (Math.random()*120);
				int b = 27;
				
				double angle; // Angle outward from center
				angle = Math.random() * 2 * Math.PI;
				
				// Check to see if this is a directional explosion, and shorten parts
				// that don't go in that direction
				// i.e. falconpunch, dont make the explosion too far ahead of character
				int x, y;
				boolean shortened = false, semishort = false;
				if(direction != null && radius > 1) {
					// Calculate as if we're moving ------> direction.
					// It really doesn't matter because we take actual direction into
					// Account when making the x and y values
					if((angle > 0.2 && angle < 1.89) || (angle > 4.38 && angle < 6.1)) {
						shortened = true;
					}
					else if(angle >= 6.1 || angle <= 0.2) {
						semishort = true;
					}
				}
				
				// If shortened, make it that way
				if(shortened) {
					// Don't include radius so its close to main character
					// Add directionAngle here, mostly so that directional rays draw right
					x = (int) Math.round(Math.cos(angle+directionAngle));
					y = (int) Math.round(Math.sin(angle+directionAngle));
				}
				else if(semishort) {
					// Don't include radius so its close to main character
					// Add directionAngle here, mostly so that directional rays draw right
					x = (int) Math.round(Math.cos(angle+directionAngle) * 2);
					y = (int) Math.round(Math.sin(angle+directionAngle) * 2);
				}
				// Otherwise computer normally
				else {
					// Compute x and y values using trig
					// Add directionAngle here, mostly so that directional rays draw right
					x = (int) Math.round(Math.cos(angle+directionAngle) * radius);
					y = (int) Math.round(Math.sin(angle+directionAngle) * radius);
				}
				
				//System.out.println("Creating Color("+r+","+g+","+b+") at explosionSquares["+x+"+"+arrsize/2+"]["+y+"+"+arrsize/2+"], size "+size);
				explosionSquares[x+arrsize/2][y+arrsize/2] = new Color(r, g, b);
			}
		}
		
		sizeindex ++;
	}
}
