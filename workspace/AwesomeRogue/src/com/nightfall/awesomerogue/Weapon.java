package com.nightfall.awesomerogue;

public class Weapon {

	private int damage;
	private int range;
	
	public Weapon(int damage, int range) {
		this.setDamage(damage);
		this.setRange(range);
	}
	
	public void attack(Character character) {
		character.getHit(damage);
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}
}
