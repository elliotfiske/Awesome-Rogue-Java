package com.nightfall.awesomerogue;

public class LevelTile { 	
	//list of tile types
	public static final int FLOOR = 0;

	public boolean visible = false;
	
	public boolean[] walls;
	
	public int type;
	
	//ID used for map generation
	private int id;
	
	public LevelTile(int type, boolean[] walls) {
		this.type = type;

		this.walls = walls;
	}

	public void doAction() {

	}
}