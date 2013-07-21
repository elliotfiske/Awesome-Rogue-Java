package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class Weapon {

	private int damage;
	private int range;
	
	// See bottom of class for the projectile class.
	// I don't think it'll be useful anywhere else,
	// So it's a private class
	private Damager projectile;
	
	private int counter;
	
	public Weapon(int damage, int range) {
		this.setDamage(damage);
		this.setRange(range);
		
		projectile = new Damager();
	}
	
	// Draw the attack! Return true if the animation is complete
	public boolean draw(Graphics2D g2, int camX, int camY) {
		return projectile.draw(g2, camX, camY);
	}
	
	public void attack(Point position, Point direction) {
		projectile.x = position.x;
		projectile.y = position.y;
		projectile.dx = direction.x;
		projectile.dy = direction.y;
		projectile.drawing = true;
		projectile.dist = 0;
		
		counter = 0;
	}

	public void update(Tile[][] map, Character[][] entities) {
		projectile.update(map, entities);
	}
	
	//public void update(Character character) {
		//character.getHit(damage, null);
	//}

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
	
	/** Damager class
	 * 
	 * @author Thomas
	 *
	 * Used by the weapon to manage its various projectiles and such
	 */
	private class Damager {
		public int x, y;
		public int dx, dy;
		public boolean drawing;
		public int dist;
		
		// Returns true if the projectile is dead
		public boolean draw(Graphics2D g2, int camX, int camY) {
			if(drawing) {
				g2.drawString("$", ((x-camX)*12), ((y-camY)*12+12));
			}
			return drawing;
		}
		
		public void update(Tile[][] map, Character[][] entities) {
			if(counter < 5) {
				counter ++;
				return;
			}
			
			counter = 0;
			
			int targetX = x + dx;
			int targetY = y + dy;
			
			if(!map[targetX][targetY].blocker && entities[targetX][targetY] == null) {
				x = targetX;
				y = targetY;
			}
			else {
				if(entities[targetX][targetY] != null) {
					entities[targetX][targetY].getHit(damage, map, entities);
				}
				drawing = false;
			}
			
			dist ++;
			if(dist > range) drawing = false;
		}
	}
}
