package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DrillDozerEffect extends Effect {

	int x, y;
	Point direction;
	ArrayList<Tile> crackingTiles;
	
	long elapsedTime;
	long COOLDOWN = 16;
	int iterations;
	private static final int MAX_ITERATIONS = Sprites.cracks.length - 1;
	
	

	public DrillDozerEffect(int x, int y, Point direction, Tile[][] map, Character[][] entities) {
		super("Drill Dozer Drilling");
		this.x = x;
		this.y = y;
		this.direction = direction;
		crackingTiles = new ArrayList<Tile>();
		iterations = 0;

		//Look at 3 tiles: the one we're exactly pointed at, the one clockwise from that, and the one counter
		//clockwise from that.

		//Number one:
		Tile tileToCheck = InGameState.tileAt(x + direction.x, y + direction.y);
		if(tileToCheck.type == Tile.WALL || tileToCheck.type == Tile.DOOR) {
			crackingTiles.add(tileToCheck);
		}

		//Number two:
		int newDirection = (Utility.getNumberedDirection(direction) + 1) % 8;
		Point newPoint = Utility.getPointDirection(newDirection);
		tileToCheck = InGameState.tileAt(x + newPoint.x, y + newPoint.y);
		if(tileToCheck.type == Tile.WALL || tileToCheck.type == Tile.DOOR) {
			crackingTiles.add(tileToCheck);
		}

		//Number 3:
		newDirection = Utility.getNumberedDirection(direction) - 1;
		if(newDirection == -1) { newDirection = 7; }
		newPoint = Utility.getPointDirection(newDirection);
		tileToCheck = InGameState.tileAt(x + newPoint.x, y + newPoint.y);
		if(tileToCheck.type == Tile.WALL || tileToCheck.type == Tile.DOOR) {
			crackingTiles.add(tileToCheck);
		}
	}

	@Override
	public void iterate(long deltaTime) {
		//Check the cooldown
		elapsedTime += deltaTime;
		if(elapsedTime < COOLDOWN) {
			return;
		}
		elapsedTime = 0;
		
		iterations++;
		
		if(iterations >= MAX_ITERATIONS) {
			for(Tile crackTile : crackingTiles) {
				int drillX = crackTile.x;
				int drillY = crackTile.y;
				InGameState.demolish(drillX, drillY);
				InGameState.map[drillX][drillY].seen = true;
			}
			setRunning(false);
		}
	}

	public void render(Graphics2D g2) {
		for(Tile crackTile : crackingTiles) {
			int drillX = crackTile.x;
			int drillY = crackTile.y;
			g2.drawImage(Sprites.cracks[iterations], (drillX - InGameState.CAMERA_PX_X) * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_X, 
					(drillY - InGameState.CAMERA_PX_Y) * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_Y, null);
		}
	}

	public void reverse() {

	}

}
