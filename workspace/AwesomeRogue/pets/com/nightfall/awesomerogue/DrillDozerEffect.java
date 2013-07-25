package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class DrillDozerEffect extends Effect {

	int x, y;
	Point direction;
	Tile[][] map;
	Character[][] entities;
	int[][] crackedTiles;
	
	public DrillDozerEffect(int x, int y, Point direction, Tile[][] map, Character[][] entities) {
		super("Drill Dozer Drilling");
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.map = map;
		this.entities = entities;
		crackedTiles = new int[map.length][map[0].length];
	}
	
	@Override
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		//Look at 3 tiles: the one we're exactly pointed at, the one clockwise around us, and the one counter
		//clockwise around us.
		
		boolean done = true;
		
		//Number one:
		Tile myTile = InGameState.tileAt(x + direction.x, y + direction.y);
		if(myTile.type == Tile.WALL || myTile.type == Tile.DOOR) {
			done &= drill(x + direction.x, y + direction.y, g2);
		}
		
		//Number two:
		int newDirection = (Enemy.getNumberedDirection(direction) + 1) % 8;
		Point newPoint = Enemy.getPointDirection(newDirection);
		myTile = InGameState.tileAt(x + newPoint.x, y + newPoint.y);
		if(myTile.type == Tile.WALL || myTile.type == Tile.DOOR) {
			done &= drill(x + newPoint.x, y + newPoint.y, g2);
		}
		
		//Number 3:
		newDirection = Enemy.getNumberedDirection(direction) - 1;
		if(newDirection == -1) { newDirection = 7; }
		newPoint = Enemy.getPointDirection(newDirection);
		myTile = InGameState.tileAt(x + newPoint.x, y + newPoint.y);
		if(myTile.type == Tile.WALL || myTile.type == Tile.DOOR) {
			done &= drill(x + newPoint.x, y + newPoint.y, g2);
		}
		
		//If all the tiles have been drilled, finish drilling for this turn
		if(done) {
			setRunning(false);
		}
	}

	/**
	 * "Drills" selected tile.  Will push enemies out of way + damage them, and melt walls that are in the way. Also
	 * iterates the "cracked tile" effect.
	 * @param drillX
	 * @param drillY
	 * @return true when it's done, false if it's not done displaying the drilling animation.
	 */
	private boolean drill(int drillX, int drillY, Graphics2D g2) {
		if(entities[drillX][drillY] instanceof Enemy) {
			//force march 'em TODO
		}
		
		crackedTiles[drillX][drillY]++;
		
		if(crackedTiles[drillX][drillY] >= Sprites.cracks.length - 1) {
			InGameState.demolish(drillX, drillY);
			map[drillX][drillY].seen = true;
			return true;
		}
		
		g2.drawImage(Sprites.cracks[crackedTiles[drillX][drillY]], (drillX - InGameState.CAMERA_X) * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_X, 
				(drillY - InGameState.CAMERA_Y) * InGameState.TILE_SIZE + InGameState.INGAME_WINDOW_OFFSET_Y, null);
		
		return false;
	}

	@Override
	public void render(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

}
