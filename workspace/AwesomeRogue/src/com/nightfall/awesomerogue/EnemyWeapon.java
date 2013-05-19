package com.nightfall.awesomerogue;

public class EnemyWeapon extends Weapon {
	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;
	public static final int MUSHROOM = 6;
	
	public EnemyWeapon(int whichEnemy) {
		super(0,0);
		switch(whichEnemy) {
		case RAT:
		case ZOMBIE:
			setDamage(5);
			break;
		case SKELETON:
			setDamage(15);
			setRange(5);
			break;
		case WIZARD:
			setDamage(50);
			break;
		}
	}
}
