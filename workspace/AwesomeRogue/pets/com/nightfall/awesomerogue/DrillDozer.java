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
		//Using "super" should make so that our override down there vvv doesn't get called unless
		//we're force marching.
		super.moveTo(direction.x, direction.y);
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
	}
	
	//If you falcon punch your drill dozer it carves a path even faster which is Awesome
	@Override
	public void forceMarch(int dx, int dy) {
		super.forceMarch(dx, dy);
		//Changes direction to match the force march
		this.direction = new Point(Utility.sign(dx), Utility.sign(dy));
		System.out.println("Your drill dozer flies through the air, frantically carving a path through the stone!");
	}

	@Override
	//This is overridden so that a force marching drill dozer can carve a path through the stone.
	public void moveTo(int newX, int newY, Character[][] entities, Tile[][] map) {
		super.moveTo(newX, newY);
		InGameState.waitOn(new DrillDozerEffect(getX(), getY(), direction, map, InGameState.getEntities()));
	}

	public String getName() {
		return "Drill Dozer";
	}
	
	public int getWeight() {
		return 1;
	}
}