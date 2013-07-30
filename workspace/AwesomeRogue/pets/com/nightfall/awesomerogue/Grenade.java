package com.nightfall.awesomerogue;

import java.awt.Point;

public class Grenade extends Pet {
	private Point direction;
	private int timer; 
	
	public Grenade(int x, int y, Point direction) {
		super(x, y, "G");
		
		this.direction = direction;
		forceMarch(direction.x*4, direction.y*4);
		timer = 3;
		
		setAltitude(1);
	}

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		timer --;
		if(timer <= 0) {
			die();
			InGameState.addEvent(new Event.Despawn(this));
			InGameState.waitOn(new Explosion(getX(), getY()));
			System.out.println("x and y of grenade: " + getX() + ", " + getY());
		}
	}
	
	public int getWeight() {
		return 5;
	}
	
	public String getName() {
		return "Grenade";
	}
	
	public void undoTurn() {
		timer++;
		if(timer == 1) {
			dead = false;
			InGameState.addPet(this);
		}
	}
}
