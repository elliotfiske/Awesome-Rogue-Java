package com.nightfall.awesomerogue;

import java.awt.Color;

public class Tile { 	
	
	//list of tile types
	public static final int FLOOR = 0;
	public static final int WALL = 1;

	public boolean blocker = false;
	public boolean visible = false, seen = false;
	
	public int type;
	
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
		case FLOOR:
			blocker = false;
			break;
		case WALL:
			blocker = true;
			break;
		}

		this.type = type;
	}

	public Tile(int type, int id) {
		this(type);
		this.id = id;
	}
	
	public Tile(int type, int id, int x, int y) {
		this(type, id);
		this.x = x;
		this.y = y;
	}

	public void doAction() {

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
}