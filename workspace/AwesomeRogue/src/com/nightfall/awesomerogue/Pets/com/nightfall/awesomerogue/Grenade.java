package com.nightfall.awesomerogue;

import java.awt.Point;

public class Grenade extends Character {
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
			InGameState.waitOn(new Explosion(getX(), getY()));
		}
	}
	
	public int getWeight() {
		return 5;
	}
}
