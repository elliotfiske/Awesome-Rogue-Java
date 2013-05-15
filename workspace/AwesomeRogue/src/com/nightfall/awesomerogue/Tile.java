package com.nightfall.awesomerogue;

public class Tile { 	
	
	//list of tile types
	public static final int FLOOR = 0;
	public static final int WALL = 1;

	public boolean blocker = false;
	public boolean visible = false, seen = false;
	
	public int type;
	
	//ID used for map generation
	private int id;
	
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

	public void doAction() {

	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
}