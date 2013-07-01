package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class DrillDozerEffect extends Effect {

	int x, y;
	Point direction;
	Tile[][] map;
	Character[][] entities;
	
	public DrillDozerEffect(int x, int y, Point direction, Tile[][] map, Character[][] entities) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.map = map;
		this.entities = entities;
	}
	
	@Override
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		//Look at 3 tiles: the one we're exactly pointed at, the one clockwise around us, and the one counter
		//clockwise around us.
		
		//Number one:
		drill(x + direction.x, y + direction.y);
		
		//Number two:
		int newDirection = (Enemy.getNumberedDirection(direction) + 1) % 8;
		Point newPoint = Enemy.getPointDirection(newDirection);
		drill(x + newPoint.x, y + newPoint.y);
		
		//Number 3:
		newDirection = Enemy.getNumberedDirection(direction) - 1;
		if(newDirection == -1) { newDirection = 7; }
		newPoint = Enemy.getPointDirection(newDirection);
		drill(x + newPoint.x, y + newPoint.y);
	}

	/**
	 * "Drills" selected tile.  Will push enemies out of way + damage them, and melt walls that are in the way.
	 * @param drillX
	 * @param drillY
	 */
	private void drill(int drillX, int drillY) {
		
	}

	@Override
	public void render(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

}
