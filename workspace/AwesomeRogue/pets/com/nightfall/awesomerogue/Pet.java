package com.nightfall.awesomerogue;

public abstract class Pet extends Character {

	public Pet(int x, int y, String character) {
		super(x, y, character);
	}
	
	public abstract void undoTurn();
	
}
