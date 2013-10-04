package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Enemy extends Character {

	public static final int MUSHROOM    = 0;
	public static final int RAT         = 1;
	public static final int ZOMBIE      = 2;
	public static final int SKELETON    = 3;
	public static final int TOWERSHROOM = 4;
	public static final int HEALSHROOM  = 5;
	public static final int OGRE        = 6;
	public static final int WIZARD      = 7;

	public static final String[] enemyIcons = {"M", "r", "R", "Z", "S", "W", "m"};
	/** A list of all the enemy images. Populated by the Sprite.loadSprites() method */
	public static BufferedImage[] images = new BufferedImage[WIZARD + 1];

	private int whichEnemy;
	protected int health = 0;
	String icon;
	int weight;
	/** Enemies have a speed/5 chance of moving */
	int speed;

	/** An enemy is stuck in place until "stunned" is 0, decremented each turn. */
	int stunned;
	/** Frozen is like stunned, but bluuue */
	int frozen;

	private int xBounty, yBounty;
	private Tile bounty;

	/** smartMove stops if a enemy has been seen that WAS NOT seen before. If smartSeen is true
	 * then the enemy has been visible since smartMove began. */
	public boolean smartSeen;

	public Enemy /* number one */ (int x, int y, int whichEnemy) {
		super(x, y, "s");
		this.whichEnemy = whichEnemy;

		smartSeen = false;
		stunned = frozen = 0;
		icon = super.character = enemyIcons[whichEnemy];
	}

	/**
	 * This method returns a new Enemy subclass object at the specified coordinates.
	 * You should use it instead of public Enemy().	 */
	public static Enemy makeEnemy(int x, int y, int whichEnemy) {
		switch(whichEnemy) {
		case RAT:
			return new Rat(x, y);
		case MUSHROOM:
			return new Mushroom(x, y);
		case ZOMBIE:
			return new Zombie(x, y);
		case SKELETON:
			return new Skeleton(x, y);
		case TOWERSHROOM:
			return new Towershroom(x, y);
		case HEALSHROOM:
			return new Healshroom(x, y);
		case OGRE:
			return new Ogre(x, y);
		case WIZARD:
			return new Wizard(x, y);
		}
		
		throw new PANICEVERYTHINGISBROKENERROR("You specified a weird enemy constant! Value: " + whichEnemy);
	}
	
	public void setBounty(int x, int y, Tile bountyTile) {
		xBounty = x;
		yBounty = y;
		bounty = bountyTile;
	}

	public int getType() { return whichEnemy; }

	public void move(int dx, int dy) {
		initPos(dx, dy);
		//sanity check here.
	}

	@Override
	public void takeTurn(MainCharacter mainChar) {
		if(stunned > 0) {
			stunned--;
			return;
		}

		if(frozen > 0) {
			frozen--;
			return;
		}

		//First off, check if the enemy even CAN move:
		double chanceOfMoving = speed / 5;
		if(chanceOfMoving < Math.random()) {
			return;
		}

		Utility.pathToPointAndMove(this, mainChar.getX(), mainChar.getY());
	}

	/**
	 * Render the enemy to the screen.
	 * 
	 * @param g2 The Graphics2D handle it uses to draw itself.
	 * @param camX Camera X offset
	 * @param camY Camera Y offset
	 */
	@Override
	public void draw(Graphics2D g2, int camX, int camY) {
		if(frozen > 0) {
			InGameState.imgSFX.drawReddererImage(g2, image, camX, camY, 1f);
			g2.drawImage(images[whichEnemy], x * InGameState.TILE_SIZE - camX,
					y * InGameState.TILE_SIZE - camY, null);
		} else {
			g2.drawImage(images[whichEnemy], x * InGameState.TILE_SIZE - camX,
				y * InGameState.TILE_SIZE - camY, null);
		}
	}

	/**
	 * Enemies have hit points.
	 * 
	 * This takes them away.  It also checks if they're dead.
	 * 
	 * @param damage How much damage to do to the monster.
	 */
	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		health -= damage;
		InGameState.addEvent(new Event.DamageTaken(this, damage));

		if(health <= 0) {
			die();
			entities[x][y] = null;

			if(bounty != null) {
				map[xBounty][yBounty] = bounty;
			}

			if(whichEnemy == WIZARD) {
				//win
			}

			System.out.println("The " + name + " is slain!");

			//TODO!
			//InGameState.addEvent("killed" + getName() + "at" + x + "x" + y);
		}
	}

	public void stun(int turns) {
		if(stunned > turns) return;
		stunned = turns;
	}

	public void freeze(int turns) {
		if(frozen > turns) return;
		frozen = turns;
	}

	/**
	 * Heals an enemy. I'd like to implement some kind of enemy-healer, because
	 * those guys are so wonderfully annoying and it's super satisfying to
	 * mash their heads into the ground.
	 * 
	 * @param health How much healing?
	 */
	public void getHealed(int healing) {
		this.health += healing;

		InGameState.healText(x, y, healing, true);
	}

	public int getWeight() {
		return weight;
	}

	public void die() {
		super.die();
		InGameState.removeEnemy(this);
	}

	public static class Rat extends Enemy {
		public Rat(int x, int y) {
			super(x, y, RAT);
			super.health = 10;
			weight = 2;
			speed = 5;
		}

		@Override
		public void takeTurn(MainCharacter mainChar) {
			if(stunned > 0) {
				stunned--;
				return;
			}

			if(frozen > 0) {
				frozen--;
				return;
			}

			//First off, check if the enemy even CAN move:
			double chanceOfMoving = speed / 5;
			if(chanceOfMoving < Math.random()) {
				return;
			}

			//Rats move randumbly
			if(Math.random() < 0.3) {
				Utility.moveRandomly(this);
			} else {
				Utility.pathToPointAndMove(this, mainChar.getX(), mainChar.getY());
			}
		}
	}

	@Override
	public BufferedImage getSprite() {
		//TODO: Change this so that if enemy is frozen, it returns a blue version
		return Sprites.enemyImages[whichEnemy];
	}
	
	public static class Mushroom extends Enemy {
		public Mushroom(int x, int y) {
			super(x, y, MUSHROOM);
			super.health = 5;
			weight = 1;
			speed = 0;
		}
	}

	public static class Zombie extends Enemy {
		public Zombie(int x, int y) {
			super(x, y, ZOMBIE);
			super.health = 15;
			weight = 7;
			speed = 3;
		}
	}

	public static class Skeleton extends Enemy	 {
		public Skeleton(int x, int y) {
			super(x, y, SKELETON);
			super.health = 15;
			weight = 3;
			speed = 4;
		}

		//TODO: Throw me a bone! (As in, an attack. Where he throws a bone.)
	}

	public static class Towershroom extends Enemy {
		public Towershroom(int x, int y) {
			super(x, y, TOWERSHROOM);
			super.health = 10;
			weight = 3;
			speed = 0;
		}
		
		//TODO: Shoot on TakeTurn
	}

	public static class Healshroom extends Enemy {
		public Healshroom(int x, int y) {
			super(x, y, HEALSHROOM);
			super.health = 10;
			weight = 3;
			speed = 0;
		}
		
		//TODO: Heal on TakeTurn
	}

	public static class Ogre extends Enemy {
		public Ogre(int x, int y) {
			super(x, y, OGRE);
			super.health = 20;
			weight = 15;
			speed = 2;
		}
	}

	public static class Wizard extends Enemy {
		public Wizard(int x, int y) {
			super(x, y, WIZARD);
			super.health = 50;
			weight = 10;
			speed = 5;
		}
	}
}
