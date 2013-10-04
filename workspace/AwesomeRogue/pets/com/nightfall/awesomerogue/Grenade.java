package com.nightfall.awesomerogue;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class Grenade extends Pet {
	private int timer; 
	
	public Grenade(int x, int y, Point direction) {
		super(x, y, "G");
		forceMarch(direction.x*4, direction.y*4);
		timer = 3;
		
		setAltitude(1);
	}

	@Override
	public void takeTurn(MainCharacter mainChar) {
		timer --;
		if(timer <= 0) {
			die();
			InGameState.addEvent(new Event.Despawn(this));
			InGameState.waitOn(new Explosion(getX(), getY()));
			System.out.println("x and y of grenade: " + getX() + ", " + getY());
		}
	}

	@Override
	public int getWeight() {
		return 5;
	}

	@Override
	public String getName() {
		return "Grenade";
	}

	@Override
	public void undoTurn() {
		timer++;
		if(timer == 1) {
			dead = false;
			InGameState.addPet(this);
		}
	}
	
	@Override
	public BufferedImage getSprite() {
		return Sprites.petImages[Pet.GRENADE];
	}
}
