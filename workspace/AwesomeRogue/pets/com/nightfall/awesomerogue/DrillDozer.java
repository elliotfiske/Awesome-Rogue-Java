package com.nightfall.awesomerogue;

import java.awt.Point;

public class DrillDozer extends Pet {
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
		
		if(InGameState.tileAt(getX() + direction.x, getY() + direction.y).type == Tile.IMPASSABLE) {
			System.out.println("Your drill dozer clinks ineffectively against the bedrock.");
		}
		
		InGameState.waitOn(new DrillDozerEffect(getX(), getY(), direction, map, InGameState.getEntities()));
		
		if(lifespan <= 0) {
			InGameState.addEvent(new Event.Despawn(this));
			die();
			System.out.println("Your Drill Dozer shudders to a halt, and conveniently teleports itself " +
					"back into your pocket.");
		}
	}

	/**
	 * Rewind a drill dozer turn. The map-changes are handled by InGameState, so no need to worry about 'em.
	 */
	public void undoTurn() {
		lifespan++;
		if(lifespan == 1) { //come back from the dead
			dead = false;
			InGameState.addPet(this);
		}
		move(-direction.x, direction.y, InGameState.map, InGameState.getEntities());
	}
	
	//If you falcon punch your drill dozer it carves a path even faster which is Awesome
	@Override
	public void forceMarch(int dx, int dy) {
		super.forceMarch(dx, dy);
		System.out.println("Your drill dozer flies through the air, frantically carving a path through the stone!");
	}

	@Override
	public void update(Tile[][] map, Character[][] entities) {
		super.update(map, entities);
		if(isForceMarching()) {
			InGameState.waitOn(new DrillDozerEffect(getX(), getY(), direction, map, InGameState.getEntities()));
		}
	}

	public String getName() {
		return "Drill Dozer";
	}
	
	public int getWeight() {
		return 1;
	}
}