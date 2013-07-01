package com.nightfall.awesomerogue;

import java.awt.Point;

public class DrillDozer extends Character {
	private Point direction;
	private int lifespan;
	
	public DrillDozer(int x, int y, Point direction) {
		super(x, y, "X");
		
		this.direction = direction;
		lifespan = 5;
		
		setAltitude(1);
	}

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		lifespan--;
		
		move(direction.x, direction.y, map, idk lol);
		
		if(lifespan <= 0) {
			die();
			System.out.println("Your Drill Dozer shudders to a halt, and conveniently teleports itself " +
					"back into your pocket.");
		}
	}
}
