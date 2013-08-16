package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Character {
	public static final int VISIONRANGE = 35;
	
	protected int x;
	protected int y;
	
	protected int room;
	String character = "default character?!?";
	
	private int altitude;	// 0 is default, meaning it's on the ground
	
	private boolean forceMarch;
	private Point forceMarchTo;
	
	private Weapon currentWeapon;
	private boolean drawingAttack;
	
	protected boolean dead;
	
	public Character(int x, int y, String character) {
		initPos(x, y);
		this.character = character;
		
		dead = false;
	}
	
	public void initPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void initPos(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public void move(int dx, int dy, Tile[][] map, Character[][] entities) {
		int targetX = x + dx;
		int targetY = y + dy;
		
		if(!InGameState.tileAt(targetX, targetY).isBlocker()) {
			moveTo(targetX, targetY, entities, map);
			room = map[x][y].room;
		}
	}
	
	public int getRoom() { return room; }
	public void setRoom(int room) { this.room = room; }

	public int getAltitude() { return altitude; }
	public void setAltitude(int altitude) { this.altitude = altitude; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public boolean isForceMarching() { return forceMarch; }
	
	public void setCurrentWeapon(Weapon weapon) { currentWeapon = weapon; }
	public Weapon getCurrentWeapon() { return currentWeapon; }
	
	public void takeTurn(MainCharacter mainChar, Tile[][] map) { }
	
	// Set the weapon to attack in a certain direction.
	// This does not do any damage inherently, in case
	// You punch the air or something. The weapon handles that.
	public void attack(Point direction) {
		
		//if(enemy.getClass() == this.getClass()) return; // Friendly fire!
		drawingAttack = true;
		//InGameState.waitOn("animation"); //TODO: TURN ATTACKS INTO EFFECTS
		
		// Tell the weapon both where you are attacking from and what
		// DIrection to attack in
		currentWeapon.attack(new Point(x, y), direction);
	}

	public void forceMarch(int dx, int dy) {
		forceMarch = true;
		forceMarchTo = new Point(x + dx, y + dy);
		InGameState.waitOn(new ForceMarch(this, forceMarchTo));
	}
	
	public void update(Tile[][] map, Character[][] entities) {
		if(drawingAttack) {
			currentWeapon.update(map, entities);
		}
	}
	
	/**
	 * Tests if a character will immediately slam into a wall if they force march in the point direction
	 * @param direction dx and dy to check
	 * @return tru o fals
	 */
	public boolean canForceMarch(Point direction) {
		if(InGameState.tileAt(x + direction.x, y + direction.y).blocker) {
			return false;
		}
		
		if(InGameState.getEntities()[x + direction.x][y + direction.y] != null) {
			return InGameState.getEntities()[x + direction.x][y + direction.y].canForceMarch(direction);
		}
		
		return true;
	}
	
	/**
	 * Draw the Character to the screen.
	 * 
	 * @param g2 The Graphics2D context that will be used to draw.
	 * @param camX Where the camera is horizontally.
	 * @param camY Where the camera is vertically.
	 */
	public void draw(Graphics2D g2, int camX, int camY) {
		g2.setColor(Color.white);
		g2.drawString(character, ((x-camX)*12), ((y-camY)*12+12));
		if(drawingAttack) {
			if(!currentWeapon.draw(g2, camX, camY)) {
				drawingAttack = false;
				//InGameState.endWait("animation"); //TODO: TURN ATTACKS INTO ANIMATIONS
			}
		}
	}

	public String getName() {
		return character;
	}

	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		System.out.println("I took "+damage+" damage but I don't know how to handle it");
	}
	
	public void getHealed(int amount) {
		System.out.println("I got healed by " + amount + " but I don't know how to handle the POWEr");
	}
	
	
	public void die() { 
		dead = true; 
		Character[][] entities = InGameState.getEntities();
		entities[x][y] = null; 
	}

	public boolean dead() {
		return dead;
	}
	
	/**
	 * How heavy you are.  Important for collisions.
	 */
	public int getWeight() {
		//Defaults to 10
		return 10;
	}
	
	/**
	 * Calculates the difference between a character and this one and propels the other one away.
	 * @param c The character to push away.
	 */
	public void knockAway(Character c, int distance) {
		int dx = c.getX() - x;
		int dy = c.getY() - y;
		
		dx = (int) Math.signum((double) dx);
		dy = (int) Math.signum((double) dy);
		
		c.forceMarch(dx * distance, dy * distance);
	}
	
	/**
	 * Uses the last 3 characters of the default toString() to make a unique id.
	 * @return A sweet, sweet unique ID
	 */
	public String getID() {
		return toString().substring(toString().length() - 3);
	}
	
	/**
	 * Handles the whole "entities array" thing for us, but doesn't check walls or anything.
	 * @param newX X location to move to
	 * @param newY Y location to move to NOTE: absolute, not relative
	 */
	public void moveTo(int newX, int newY) {
		moveTo(newX, newY, InGameState.getEntities(), InGameState.map);
	}
	
	public void moveTo(int newX, int newY, Character[][] entities, Tile[][] map) {
		InGameState.addEvent(new Event.Movement(this, x, y, newX, newY));
		
		entities[x][y] = null;
		entities[newX][newY] = this;
		x = newX;
		y = newY;

		if(map != null) {
			room = map[x][y].room;
			map[x][y].doAction(this);
		}
	}

}
