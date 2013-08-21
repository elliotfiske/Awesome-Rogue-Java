package com.nightfall.awesomerogue;

import java.awt.Color;

public class Tile { 	

	//list of tile types
	public static final int VOID = -1;
	public static final int FLOOR = 0;
	public static final int WALL = 1;
	public static final int DOOR = 2;
	public static final int OPEN_DOOR = 3;
	public static final int CHEST = 4;
	public static final int IMPASSABLE = 5; //"bedrock" so to speak
	public static final int CLOSED_PIT = 6;
	public static final int OPEN_PIT = 7;

	public boolean blocker = false;
	public boolean visible = false, seen = false;

	public int type;
	public int room;

	public Color color;
	/** True if this Tile is being color-changed for debugging */
	public boolean illustrated = false;

	//ID used for map generation
	private int id;

	//where u at
	public int x;
	public int y;

	public Tile(int type) {
		switch(type) {
		case WALL:
		case DOOR:
		case CHEST:
		case IMPASSABLE:
			blocker = true;
			break;
		}

		this.type = type;
	}

	public Tile(int type, int id, int x, int y) {
		this(type);
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public Tile(int type, int x, int y) {
		this(type);
		this.x = x;
		this.y = y;
	}

	public void doAction(Character character) {
		switch(type) {		// Screw subclasses just for the doAction...
		case DOOR:
			type = OPEN_DOOR;
			blocker = false;
			break;
		case CHEST:
			if(character instanceof MainCharacter) { 
				type = FLOOR;
				blocker = false;
				((MainCharacter) character).findArtifact();
				if( ((MainCharacter) character).isHulking() ) {
					System.out.println("You gingerly open the chest with your giant hand and take out a " + "something");
				} else {
					System.out.println("You dramatically open the chest and uncover a " + "item!");
				}
			}
			break;
		case CLOSED_PIT:
			type = OPEN_PIT;
			System.out.println("You fall in a hole!");
			break;
		}
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	/**
	 * Sets the Tile to a color so I can show off sexy pathfinding algorithms and whatnot.
	 * @param color WHAT COLOR EH
	 */
	public void illustrate(Color color) {
		this.color = color;
		illustrated = true;
	}

	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}

		if(o instanceof Tile) {
			Tile t = (Tile) o;
			if(t.x == x && t.y == y) {
				return true;
			}
		}

		return false;
	}
	
	public boolean isBlocker() {
		boolean occupied = (InGameState.getEntities()[x][y] != null);
		return blocker || occupied;
	}
}