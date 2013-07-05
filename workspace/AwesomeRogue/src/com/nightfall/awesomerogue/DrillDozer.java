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
		
		move(direction.x, direction.y, map, InGameState.getEntities());
		//Each turn, the Drill Dozer looks for the three tiles in front of it and melts 'em
		//  xx           x   
		//  7x    v      x<   etc.
   		//       xxx     x
		InGameState.waitOn(new DrillDozerEffect(getX(), getY(), direction, map, InGameState.getEntities()));
		
		
		if(lifespan <= 0) {
			die();
			System.out.println("Your Drill Dozer shudders to a halt, and conveniently teleports itself " +
					"back into your pocket.");
		}
	}
	
	public String getName() {
		return "Drill Dozer";
	}
}
