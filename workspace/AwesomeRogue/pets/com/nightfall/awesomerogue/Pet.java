package com.nightfall.awesomerogue;

public abstract class Pet extends Character {

	public static int DRILL_DOZER = 0;
	public static int GRENADE = 1;
	
	public Pet(int x, int y, String character) {
		super(x, y, character);
	}
	
	public abstract void undoTurn();
	
}
