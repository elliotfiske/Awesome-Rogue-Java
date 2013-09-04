package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class FalconPunch extends Skill {
	public FalconPunch() {
		super(10);
	}

	/** returns a boolean because false means that it's fired, so don't wait up */
	public boolean prepare() {
		System.out.println("FALCONNNNNNNNNN...");
		return true;
	}

	public void activate(Point direction, MainCharacter mainChar) {
		System.out.println("PAWNCHHHHH in the direction of dx="+direction.x+", dy="+direction.y);
		mainChar.forceMarch(direction.x*4, direction.y*4);
		
		InGameState.waitOn(new Explosion(mainChar.getX(), mainChar.getY()));
	}

	public void update() {
	}

	public void draw(Graphics2D g2) {
	}
}
