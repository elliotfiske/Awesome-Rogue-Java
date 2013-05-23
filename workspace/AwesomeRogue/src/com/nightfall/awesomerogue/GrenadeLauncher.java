package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class GrenadeLauncher extends Skill {
	public GrenadeLauncher() {
		super(10);
	}

	/** returns a boolean because false means that it's fired, so don't wait up */
	public boolean prepare() {
		System.out.println("You pull the pin...");
		return true;
	}

	public void activate(Point direction, MainCharacter mainChar) {
		mainChar.getLevel().addCharacter(new Grenade(mainChar.getX(), mainChar.getY(), direction));
	}

	public void update() {
	}

	public void draw(Graphics2D g2) {
	}
}
