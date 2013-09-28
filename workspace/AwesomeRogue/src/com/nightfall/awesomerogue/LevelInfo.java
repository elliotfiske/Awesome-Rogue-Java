package com.nightfall.awesomerogue;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class LevelInfo {
  public static final int CAVE = 3;
	public static final int INTRO = 0;
	public static final int BOSS = 1;
	public static final int ROOMS = 2;
	public static final int CATACOMBS = 4;
	public static final int RUINS = 5;
	
	private static final int SEED = 37;

	//---------------------------CAVE GENERATION CONSTANTS--------------------------------//
	/**
	 * For the Cave level.  How many walls should there be adjacent to a tile for it to
	 * become a wall itself?
	 */
	private static final int SMOOTHNESS = 5;
	/**
	 * How many times should we Conway-ify the cave level?
	 */
	private static final int CAVE_ITERATIONS = 8;
	
	/** 
	 * Chance for the room generator
	 */
	private static final int ROOM_CHANCE = 60;
	private static final int ROOMSW = 20;
	private static final int ROOMSH = 20;
	public static int MAX_FEATURES = 20;
	
	/**
	 * Room types
	 */
	public static final int ROOM_BOSS = -2;
	public static final int ROOM_WEAPON = -1;
	public static final int ROOM_EMPTY = 0;
	public static final int ROOM_BASIC_ENEMY = 1;
	public static final int ROOM_NORMAL_ENEMY = 2;
	public static final int ROOM_ADVANCED_ENEMY = 3;
	public static final int ROOM_TRAPS = 4;
	
	private int[] enemyTypes;
	
	private Tile[][] map;
	private ArrayList<Enemy> enemies;
	private Point startPos;

	/**
	 * How hard it gonn be like?
	 */
	private int difficulty;
	
	public LevelInfo(int type, int difficulty) {
		int width, height;
		
		enemies = new ArrayList<Enemy>();
		switch(type) {
		case CAVE:
			width = 80;
			height = 70;

			makeCaves(width, height, difficulty);
			startPos = new Point(10, 10);
			break;
		case ROOMS:
			width = 70;
			height = 60;

			makeRooms(width, height, difficulty);
			break;
		case INTRO:
			width = 38;
			height = 35;

			makeIntro(width, height, difficulty);
			startPos = new Point(17, 5);
			break;
		case CATACOMBS:
			width = 38;
			height = 35;

			makeCatacombs(width, height, difficulty);
			startPos = new Point(17, 5);
			break;
		case RUINS:
			width = 80;
			height = 60;

			makeRuins(width, height, difficulty);
			startPos = new Point(40, 30);
			break;
		}
		
		this.difficulty = difficulty;
	}
	
	public LevelInfo(Tile[][] map, ArrayList<Enemy> enemies) {
		this.map = map;
		this.enemies = enemies;
	}
	
	public Tile[][] getMap() {
		return map;
	}
	
	public ArrayList<Enemy> getEnemies() {
		return enemies;
	}
	
	public int getDifficulty() { 
		return difficulty; 
	}
	
	public Point getStartPos() {
		return startPos;
	}
	
	private void makeCaves(int width, int height, int difficulty) {
		//Create blank map
		map = new Tile[width][height];
		
		//Make our sweet number generator
		Random numGen = new Random(SEED);

		//Representational.  Stores whether the specified tile is a floor or a wall (0 or 1 respectively).
		int[][] numMap = new int[width][height];

		//First, start with random noise
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				numMap[x][y] = numGen.nextInt(2);
				//Make sure we've got walls on all sides, though.
				if(x == width - 1 || y == height - 1) {
					numMap[x][y] = 1;
				}
			}
		}

		//Stores values temporarily (so we don't base our cellular automata on itself as it's being formed)
		int[][] tempMap = new int[width][height];

		//Loop the cellular automata process the right # of times.
		for(int iterations = 0; iterations < CAVE_ITERATIONS; iterations++) {

			//Next, look at each tile.  If it has >5 neighbors that are walls, it is now a wall! Yay!
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {

					//For each tile, check each of its neighbors.
					int adjacentWalls = 0;
					for(int dx = -1; dx < 2; dx++) {
						for(int dy = -1; dy < 2; dy++) {
							try {
								//Don't count the tile itself!
								if(dx != 0 || dy != 0) {

									if(numMap[x + dx][y + dy] == Tile.WALL ) {
										adjacentWalls++;
									}

								}
							} catch(ArrayIndexOutOfBoundsException e) { adjacentWalls++; }
						}
					}

					//adjacentWalls now contains the number of walls adjacent to the cell we're looking at.
					if(adjacentWalls >= SMOOTHNESS - 1 && numMap[x][y] == Tile.WALL) {
						tempMap[x][y] = Tile.WALL;
					} else if(adjacentWalls >= SMOOTHNESS && numMap[x][y] == Tile.FLOOR) {	
						tempMap[x][y] = Tile.WALL;
					} else {
						tempMap[x][y] = Tile.FLOOR;
					}
				}
			}

			//Copy the tempMap to the numMap
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					numMap[x][y] = tempMap[x][y];
				}
			}

		} //Here ends the for loop.



		//Next, let's see if we can connect everything so we can actually walk around the whole map.
		//Here's how my sexy algorithm works:

		//Assign every floor tile a number, starting from 0, going up by one each time.
		//If a floor tile is TOUCHING another floor tile of a LOWER NUMBER, it will become that number.
		//Thus, if there are isolated tile-sections, they will all be of a different number.

		int tileID = 0;

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == 0) {
					//tempMap will store the ID's.
					tempMap[x][y] = tileID;
					tileID++;
				} else {
					tempMap[x][y] = -1;
				}
			}
		}

		for(int iterations = 0; iterations < 1000; iterations++) {
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {

					for(int dx = -1; dx < 2; dx++) {
						for(int dy = -1; dy < 2; dy++) {
							//we've got 2 tiles to look at here, let's see
							//which is lower, eh?
							try {

								if(tempMap[x + dx][y + dy] != -1 && tempMap[x][y] != -1) {
									if(tempMap[x][y] < tempMap[x + dx][y + dy]) {
										tempMap[x + dx][y + dy] = tempMap[x][y];
									} else {
										tempMap[x][y] = tempMap[x + dx][y + dy];
									}
								}

							} catch(ArrayIndexOutOfBoundsException e) {}
						}
					}

				}
			}
		}

		//Awright.  Now we need to go about connecting all these number-islands together.

		//This guy stores lists of each of the different tileIDs.
		//So at listOfLists[37], there is a list of all the Tiles that have the ID #37.
		ArrayList<ArrayList<Tile>> listOfLists = new ArrayList<ArrayList<Tile>>(width * height);	
		
		while(listOfLists.size() < width*height) listOfLists.add(null);
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == Tile.FLOOR) {
					Tile tileForList = new Tile(Tile.FLOOR, tempMap[x][y], x, y);

					//Check if this is the first Tile we've found with this ID.
					//If it is, an error will pop up.
					try { 
						ArrayList<Tile> listOfTilesWithTheRightID = listOfLists.get( tileForList.getID() );
						listOfTilesWithTheRightID.add(tileForList);
					} catch(NullPointerException e)  {
						//This IS the first Tile with this ID.  Add it to the list.
						ArrayList<Tile> listOfTilesWithTheRightID = new ArrayList<Tile>();
						listOfTilesWithTheRightID.add(tileForList);
						listOfLists.set(tileForList.getID(), listOfTilesWithTheRightID);
					}
				}
			}
		}

		//Collapse the big ol' list into a smaller list
		ArrayList<ArrayList<Tile>> compactList = new ArrayList<ArrayList<Tile>>();
		for(ArrayList<Tile> tileList : listOfLists) {
			if(tileList != null) {
				compactList.add(tileList);
			}
		}
		
		//So I just found out apparently you can put unlimited semicolons around in Java
		//and it still compiles:
		
		; ;  ;    ;;; ; ;  ;;;  ;;; ;;;
		;;;  ;     ;  ;;;  ;;   ;;  ;;
		; ;  ;     ;  ; ;  ;;;  ; ; ;;;
		
		; ; ;;; ; ; ;;; ;;; ;;;
		;;; ;;  ;;; ; ; ; ; ; ;
		; ; ;;;  ;  ;;; ;;; ;;;
		
		//Anyways, now we've got an ArrayList containing ArrayLists of each of the tile ID groupings. Cool.
		//For each of these groupings, we want to ensure that if they are sorta close to one another,
		//they are connected.
		
		//We're gonna do this in a couple steps.
		
		//Make a list of all the matchmakings we've done so far, make sure we don't leave anyone out :I
		
		//Go through the ArrayLists of Tile ID groupings.
		//find the ID that has the most tiles
		int recordHigh = 0;
		int recordID = 0;
		for(ArrayList<Tile> tileGroup : compactList) {
			//Choose a random tile from the group.
			if(tileGroup.size() > recordHigh) {
				recordHigh = tileGroup.size();
				recordID = tileGroup.get(0).getID();
			}
			//tileGroup.get(numGen.nextInt(tileGroup.size()));
			
			//Start radiating out from this tile and stop 
		}
		
		System.out.println("WINRAR ID: " + recordID + " with " + recordHigh + " tiles!");
		
		//The new Tile system has tiles remembering where they are, so we just have to call
		//the constructor. 
		Tile t;
		
		//Convert the map of 0's and 1's to floors and walls.
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == 0) {
					//tempMap still has the ID's in it.
					//For debugging I want to print out the tile #'s.
					newTile(Tile.FLOOR, tempMap[x][y], x, y, map);
				}

				if(numMap[x][y] == 1) {
					
					newTile(Tile.WALL, 0, x, y, map);
				}
			}
		}
		
		//Make sure you CAN NEVER ESCAPE
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(x == 0 || x == width - 1) {
					t = new Tile(Tile.IMPASSABLE, x, y);
				}
				
				if(y == 0 || y == height - 1) {
					t = new Tile(Tile.IMPASSABLE, x, y);
				}
			}
		}
		
		//Plop down some enemies based on the difficulty.
		if(difficulty == 1) {
			Enemy bob = new Enemy(25, 10, Enemy.Data.RAT);
			enemies.add(bob);
		}
		
		if(difficulty == 2) {
			//Divide the map into squares of size 10x10 and plop down 3 monsters each.
			for(int xTen = 0; xTen < width; xTen += 10) {
				for(int yTen = 0; yTen < height; yTen += 10) {
					
					for(int monsterNum = 0; monsterNum < 3; monsterNum++) {
						int monsterX = numGen.nextInt(10) + xTen;
						int monsterY = numGen.nextInt(10) + yTen;
						
						//Choose a type
						Enemy.Data type = 0;
						switch(numGen.nextInt(2)) {
						case 0:
							type = Enemy.Data.MUSHROOM;
							break;
						case 1:
							type = Enemy.Data.RAT;
							break;
						}
						
						//if it's on a wall, it's outta luck.  So sad, try again next time.
						if(map[monsterX][monsterY].type != Tile.FLOOR) {
							continue;
						}
						
						Enemy newEnemy = new Enemy(monsterX, monsterY, type);
						enemies.add(newEnemy);
					}
				
				}
			}
		}
	}
	
	private void makeRooms(int width, int height, int difficulty) {
		enemyTypes = new int[] { Enemy.MUSHROOM, Enemy.RAT, Enemy.ANGRY_MUSHROOM };
		
		//Create blank map
		map = new Tile[width][height];
		
		// Create whole level (all void)
		for(int i = 0; i < width; i ++) {
			for(int j = 0; j < height; j ++) {
				if(i == 0 || j == 0 || i == width-1 || j == height-1) {
					newTile(Tile.WALL, i, j, map);
				}
				else {
					newTile(Tile.VOID, i, j, map);
				}
			}
		}
		
		Random numGen = new Random(SEED);
		makeRoom(map.length/2, map[0].length/2, ROOMSW, ROOMSH, (int) Math.pow(2, numGen.nextInt(3)), ROOM_EMPTY, numGen);
		startPos = new Point(map.length/2+2, map[0].length/2+2);
		
		int currentFeatures = 1;
		boolean weaponRoom = false;
		//then we sart the main loop
		for (int countingTries = 0; countingTries < 1000; countingTries++){
			// Quota Check
			if(currentFeatures == MAX_FEATURES) break;
			
			//start with a random wall
			int newx = 0;
			int xmod = 0;
			int newy = 0;
			int ymod = 0;
			int validTile = -1;
			//1000 chances to find a suitable object (room or corridor)..
			//(yea, i know it's kinda ugly with a for-loop... -_-')
			for (int testing = 0; testing < 1000; testing++){
				newx = numGen.nextInt(map.length-3) + 1;
				newy = numGen.nextInt(map[0].length-3) + 1;
				validTile = -1;
				//System.out.println("tempx: " + newx + "\ttempy: " + newy);
				if (map[newx][newy].type == Tile.WALL || map[newx][newy].type == Tile.FLOOR){
					//check if we can reach the place
					if (!map[newx][newy+1].blocker && map[newx][newy-1].type == Tile.VOID){
						validTile = N;
						xmod = 0;
						ymod = -1;
					}
					else if (!map[newx-1][newy].blocker && map[newx+1][newy].type == Tile.VOID){
						validTile = E; //
						xmod = +1;
						ymod = 0;
					}
					else if (!map[newx][newy-1].blocker && map[newx][newy+1].type == Tile.VOID){
						validTile = S; //
						xmod = 0;
						ymod = +1;
					}
					else if (!map[newx+1][newy].blocker && map[newx-1][newy].type == Tile.VOID){
						validTile = W; //
						xmod = -1;
						ymod = 0;
					}
 
					//check that we haven't got another door nearby, so we won't get alot of openings besides
					//each other
					if (validTile > -1){
						if (map[newx][newy-1].type == Tile.DOOR) //north
							validTile = -1;
						else if (map[newx+1][newy].type == Tile.DOOR)//east
							validTile = -1;
						else if (map[newx][newy+1].type == Tile.DOOR)//south
							validTile = -1;
						else if (map[newx-1][newy].type == Tile.DOOR)//west
							validTile = -1;
					}
 
					//if we can, jump out of the loop and continue with the rest
					if (validTile > -1) break;
				}
			}
			if (validTile > -1){
				// BOSS ROOM!
				if(currentFeatures == LevelInfo.MAX_FEATURES - 1) {
					int roomType = ROOM_BOSS;
					if(!weaponRoom && numGen.nextInt(MAX_FEATURES) < currentFeatures) {
						// Compensate for being the last room
						currentFeatures --;
						roomType = ROOM_WEAPON;
					}
					if (makeRoom((newx+xmod), (newy+ymod), ROOMSW, ROOMSH, validTile, roomType, numGen)){
						currentFeatures++; //add to our quota
 
						//then we mark the wall opening with a door
						newTile(Tile.DOOR, newx+xmod, newy+ymod, map);
 
						//clean up infront of the door so we can reach it
						newTile(Tile.FLOOR, newx, newy, map);
						
						if(roomType == ROOM_WEAPON) weaponRoom = true;
					}
				}
				//choose what to build now at our newly found place, and at what direction
				int feature = numGen.nextInt(100);
				if (feature <= ROOM_CHANCE){ //a new room
					int roomType = ROOM_EMPTY;
					
					// Random chance of currentFeatures/MAX_FEATURES ensures we get a weapon room eventually
					if(!weaponRoom && numGen.nextInt(MAX_FEATURES - 1) < currentFeatures) {
						// Compensate for being the last room
						if(roomType == ROOM_BOSS) currentFeatures --;
						roomType = ROOM_WEAPON;
					}
					else {
						roomType = numGen.nextInt(5);
					}
					if (makeRoom((newx+xmod), (newy+ymod), ROOMSW, ROOMSH, validTile, roomType, numGen)){
						currentFeatures++; //add to our quota
 
						//then we mark the wall opening with a door
						newTile(Tile.DOOR, newx+xmod, newy+ymod, map);
 
						//clean up infront of the door so we can reach it
						newTile(Tile.FLOOR, newx, newy, map);
						
						if(roomType == ROOM_WEAPON) weaponRoom = true;
					}
				}
				else { //new corridor
					if (makeCorridor((newx+xmod), (newy+ymod), ROOMSH, validTile, numGen)){
						//same thing here, add to the quota and a door
						currentFeatures++;

						newTile(Tile.DOOR, newx, newy, map);
					}
				}
			}
		}
		
		//Change all the Void tiles to Wall tiles
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				Tile t = map[i][j];
				if(t.type == Tile.VOID) {
					t.type = Tile.WALL;
				}
			}
		}
	} 
	
	private boolean makeCorridor(int x, int y, int length, int direction, Random numGen) {
		// Define the dimensions
		int len = numGen.nextInt(length-2) + 2;
		int dir = 0;
		if(direction > 0 && direction <= 8) dir = direction;
		
		Tile t;
		
		int xtemp = 0;
		int ytemp = 0;
		
		switch(dir) {
		case N:
			if(x < 0 || x > map.length) return false;
			else xtemp = x;
			
			for(ytemp = y; ytemp > (y - len); ytemp --) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xt = x - 1; xt <= x + 1; xt ++)
					if(map[xt][ytemp].type != Tile.VOID) return false; 
			}

			for(ytemp = y; ytemp > (y - len); ytemp --) {
				if(ytemp == y - len + 1)
					newTile(Tile.WALL, xtemp, ytemp, map);
				else
					newTile(Tile.FLOOR, xtemp, ytemp, map);
				newTile(Tile.WALL, xtemp, ytemp, map);
				newTile(Tile.WALL, xtemp, ytemp, map);
			}
			break;
		case E:
			if(y < 0 || y > map[0].length) return false;
			else ytemp = y;
			
			for(xtemp = x; xtemp > (x - len); xtemp --) {
				if(xtemp < 0 || xtemp > map.length) return false;
				for(int yt = y - 1; yt <= y + 1; yt ++)
					if(map[xtemp][yt].type != Tile.VOID) return false; 
			}

			for(xtemp = x; xtemp > (x - len); xtemp --) {
				if(xtemp == x - len + 1)
					newTile(Tile.WALL, xtemp, ytemp, map);
				else
					newTile(Tile.FLOOR, xtemp, ytemp, map);
				newTile(Tile.WALL, xtemp, ytemp, map);
				newTile(Tile.WALL, xtemp, ytemp, map);
			}
			break;
		case S:
			if(x < 0 || x > map.length) return false;
			else xtemp = x;
			
			for(ytemp = y; ytemp <(y + len); ytemp ++) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xt = x - 1; xt <= x + 1; xt ++)
					if(map[xt][ytemp].type != Tile.VOID) return false; 
			}

			for(ytemp = y; ytemp < (y + len); ytemp ++) {
				if(ytemp == y + len - 1)
					map[xtemp][ytemp] = new Tile(Tile.WALL, xtemp, ytemp);
				else
					map[xtemp][ytemp] = new Tile(Tile.FLOOR, xtemp, ytemp);
				map[xtemp-1][ytemp] = new Tile(Tile.WALL, xtemp, ytemp);
				map[xtemp+1][ytemp] = new Tile(Tile.WALL, xtemp, ytemp);
			}
			break;
		case W:
			if(y < 0 || y > map[0].length) return false;
			else ytemp = y;
			
			for(xtemp = x; xtemp < (x + len); xtemp ++) {
				if(xtemp < 0 || xtemp > map.length) return false;
				for(int yt = y - 1; yt <= y + 1; yt ++)
					if(map[xtemp][yt].type != Tile.VOID) return false; 
			}

			for(xtemp = x; xtemp < (x + len); xtemp ++) {
				if(xtemp == x + len - 1)
					map[xtemp][ytemp] = new Tile(Tile.WALL, xtemp, ytemp);
				else
					map[xtemp][ytemp] = new Tile(Tile.FLOOR, xtemp, ytemp);
				map[xtemp][ytemp-1] = new Tile(Tile.WALL, xtemp, ytemp);
				map[xtemp][ytemp+1] = new Tile(Tile.WALL, xtemp, ytemp);
			}
			break;
		}
		
		return true;
	}
	
	/**
	 * Make a generic room (just walls and floor for now)
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 */
	private boolean makeRoom(int x, int y, int xlength, int ylength, int direction, int roomType, Random numGen) {
		int xlen = numGen.nextInt(xlength-6) + 6;
		int ylen = numGen.nextInt(ylength-6) + 6;
		
		int dir = 0;
		if(direction > 0 && direction <= 8) dir = direction;
		
		Point topLeft = null, bottomRight = null;
		
		switch(dir) {
		case N:
			// Check to make sure we're clear
			for(int ytemp = y; ytemp > (y - ylen); ytemp --) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xtemp = (x - xlen/2); xtemp < (x + (xlen-1)/2); xtemp ++) {
					if(xtemp < 0 || xtemp > map.length) return false;
					if(map[xtemp][ytemp].type != Tile.VOID) return false; 
				}
			}
			
			// Build it!
			topLeft = 		new Point(x - xlen/2, 	y - ylen + 1);
			bottomRight = 	new Point(x + xlen+1/2, y + 1);
			break;
		case E:
			// Check to make sure we're clear
			for(int ytemp = (y - ylen/2); ytemp < (y + (ylen+1)/2); ytemp ++) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xtemp = x; xtemp < (x + xlen); xtemp ++) {
					if(xtemp < 0 || xtemp > map.length) return false;
					if(map[xtemp][ytemp].type != Tile.VOID) return false; 
				}
			}
			
			// Build it!
			topLeft = 		new Point(x, 		y - ylen);
			bottomRight = 	new Point(x + xlen, y + (ylen+1)/2);
			break;
		case S:
			// Check to make sure we're clear
			for(int ytemp = y; ytemp < (y + ylen); ytemp ++) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xtemp = (x - xlen/2); xtemp < (x + (xlen+1)/2); xtemp ++) {
					if(xtemp < 0 || xtemp > map.length) return false;
					if(map[xtemp][ytemp].type != Tile.VOID) return false; 
				}
			}
			
			// Build it!
			topLeft = 		new Point(x - xlen/2, 		y);
			bottomRight = 	new Point(x + (xlen+1)/2, 	y + ylen);
			break;
		case W:
			// Check to make sure we're clear
			for(int ytemp = (y - ylen/2); ytemp < (y + (ylen+1)/2); ytemp ++) {
				if(ytemp < 0 || ytemp > map[0].length) return false;
				for(int xtemp = x; xtemp > (x - xlen); xtemp --) {
					if(xtemp < 0 || xtemp > map.length) return false;
					if(map[xtemp][ytemp].type != Tile.VOID) return false; 
				}
			}
			
			// Build it!
			topLeft = 		new Point(x - xlen + 1, y - ylen/2);
			bottomRight = 	new Point(x + 1, 		y + (ylen+1)/2);
			break;
		}

		if(topLeft != null) {
			for(int i = topLeft.x; i < bottomRight.x; i ++) {
				for(int j = topLeft.y; j < bottomRight.y; j ++) {
					if(i == topLeft.x || i == bottomRight.x - 1 ||
					   j == topLeft.y || j == bottomRight.y - 1) {
						map[i][j] = new Tile(Tile.WALL, i, j);
					}
					else {
						map[i][j] = new Tile(Tile.FLOOR, i, j);
					}
				}
			}
		}
		
		// Get just the interior for room designing
		topLeft.x ++;
		topLeft.y ++;
		bottomRight.x --;
		bottomRight.y --;
		
		int w = (bottomRight.x - topLeft.x);
		int h = (bottomRight.y - topLeft.y);
		
		switch(roomType) {
		case ROOM_WEAPON:
			Point center = new Point(w / 2, h / 2);
			map[center.x][center.y] = new Tile(Tile.CHEST, center.x, center.y);
			break;
		case ROOM_BOSS:
			break;
		case ROOM_BASIC_ENEMY:
			for(int i = 0; i < 3; i ++) {
				Enemy e = new Enemy(numGen.nextInt(w) + topLeft.x, numGen.nextInt(h) + topLeft.y, enemyTypes[0]);
				enemies.add(e);
			}
			break;
		case ROOM_NORMAL_ENEMY:
			for(int i = 0; i < 3; i ++) {
				Enemy e = new Enemy(numGen.nextInt(w) + topLeft.x, numGen.nextInt(h) + topLeft.y, enemyTypes[1]);
				enemies.add(e);
			}
			break;
		case ROOM_ADVANCED_ENEMY:
			for(int i = 0; i < 3; i ++) {
				Enemy e = new Enemy(numGen.nextInt(w) + topLeft.x, numGen.nextInt(h) + topLeft.y, enemyTypes[1]);
				enemies.add(e);
			}
			break;
		case ROOM_TRAPS:
			for(int i = 0; i < 5; i ++) {
				map[numGen.nextInt(w) + topLeft.x][numGen.nextInt(h) + topLeft.y] = new Tile(Tile.CLOSED_PIT, numGen.nextInt(w) + topLeft.x, numGen.nextInt(h) + topLeft.y);
			}
			break;
		case ROOM_EMPTY:
		default:
			break;
		}
		
		return true;
	}
	
	private void makeRuins(int width, int height, int difficulty) {
		//Create blank map
		map = new Tile[width][height];
		
		// Create whole level (all floors)
		for(int i = 0; i < width; i ++) {
			for(int j = 0; j < height; j ++) {
				if(i == 0 || j == 0 || i == height-1 || j == height-1) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
				else {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
			}
		}
		
		int ROOMSIZE = 16;
		
		// Create a bunch of random rooms with a corridor between each
		int pastRoomX = -1;
		int pastRoomY = -1;
		for(int roomNum = 0; roomNum < 15; roomNum ++) {
			int cx = (int) (Math.random() * (width - ROOMSIZE)) + ROOMSIZE / 2;
			int cy = (int) (Math.random() * (height - ROOMSIZE)) + ROOMSIZE / 2;
			makeRuinRoom(cx - ROOMSIZE / 2, cy - ROOMSIZE / 2, cx + ROOMSIZE / 2, cy + ROOMSIZE / 2);
			if(pastRoomX != -1) {
				makeRuinCorridor(cx, pastRoomX, cy, pastRoomY);
			}
			pastRoomX = cx;
			pastRoomY = cy;
		}
		 /*
		// DOOOOOOOORS
		// Create a door wherever there is a T or H intersection
		for(int i = 0; i < width; i ++) {
			for(int j = 0; j < height; j ++) {
				// ? o ?
				// x + x
				// ? o ?
				if((i == 0 || map[i-1][j].blocker) && (i+1 == map.length || map[i+1][j].blocker)) {
					if(j > 0 && j < map[0].length - 1 && !map[i][j-1].blocker && !map[i][j+1].blocker) {
						if(!map[i-1][j-1].blocker || !map[i-1][j+1].blocker || !map[i+1][j-1].blocker || !map[i+1][j+1].blocker) {
							map[i][j] = new Tile(Tile.DOOR, i, j);
						}
					}
				}
				
				// o x o
				// o + o
				// o x o
				if((j == 0 || map[i][j-1].blocker) && (j+1 == map[0].length || map[i][j+1].blocker)) {
					if(i > 0 && i < map.length - 1 && !map[i-1][j].blocker && !map[i+1][j].blocker) {
						if(!map[i-1][j-1].blocker || !map[i-1][j+1].blocker || !map[i+1][j-1].blocker || !map[i+1][j+1].blocker) {
							map[i][j] = new Tile(Tile.DOOR, i, j);
						}
					}
				}
			}
		}*/
	}
	
	/**
	 * Make a generic room (just walls and floor for now)
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 */
	private void makeRuinRoom(int minx, int miny, int maxx, int maxy) {
		if(minx < 0) minx = 0;
		if(miny < 0) miny = 0;
		if(maxx >= map.length) maxx = map.length - 1;
		if(maxy >= map[0].length) maxy = map[0].length - 1; 
		for(int i = minx; i < maxx; i ++) {
			for(int j = miny; j < maxy; j ++) {
				if(i == minx || i == maxx-1 || j == miny || j == maxy - 1) {
					map[i][j] = new Tile(Tile.WALL, i, j);	
				}
				else {
					map[i][j] = new Tile(Tile.FLOOR, i, j);
				}
			}
		}
	}
	
	/**
	 * Make a corridor between the center of 2 rooms.
	 * @param cx1 - Center of first room (x)
	 * @param cx2 - Center of second room (x)
	 * @param cy1 - Center of first room (y)
	 * @param cy2 - Center of second room (y)
	 */
	private void makeRuinCorridor(int cx1, int cx2, int cy1, int cy2) {
		if(cx1 > cx2) {
			// Rrrrrrrrrrrandom!
			// Determines whether it goes like
			//
			//    ___________|----|                       |----|
			//   | __________|    |                       |    |
			//   ||          |----|                       |----|
			// |----|                  or |----|____________||
			// |    |                     |    |_____________|
			// |----|                     |----|
			if(Math.random() >= 0.5) { 
				//
				//                 |----|
				//                 |    |
				//                 |----|
				// |----|____________||
				// |    |_____________|
				// |----|
				for(int i = cx2; i < cx1; i ++) {
					if(map[i][cy2].blocker) {
						map[i][cy2] = new Tile(Tile.FLOOR, i, cy2);
						if(!map[i-1][cy2].blocker && !map[i+1][cy2].blocker && map[i][cy2-1].blocker && map[i][cy2+1].blocker) {
							map[i][cy2] = new Tile(Tile.DOOR, i, cy2);
						}
						//System.out.println(i+", "+cx2+" is now floor");
					}
				}
				// If cy1 > cy2, switch them so we only have to use one for statement
				if(cy1 > cy2) {
					int t = cy1;
					cy1 = cy2;
					cy2 = t;
				}
				
				for(int j = cy1; j < cy2; j ++) {
					System.out.println("Flooring at "+cx1 + ", "+j);
					if(map[cx1][j].blocker) {
						map[cx1][j] = new Tile(Tile.FLOOR, cx1, j);
						if(!map[cx1][j-1].blocker && !map[cx1][j+1].blocker && map[cx1-1][j].blocker && map[cx1+1][j].blocker) {
							map[cx1][j] = new Tile(Tile.DOOR, cx1, j);
						}
					}
				}
			}
			else { 
				//
				//    ___________|----|
				//   | __________|    |
				//   ||          |----|  
				// |----|                
				// |    |                
				// |----|              
				for(int i = cx2; i < cx1; i ++) {
					if(map[i][cy1].blocker) {
						map[i][cy1] = new Tile(Tile.FLOOR, i, cy1);
						//   x
						// o + o
						//   x
						if(!map[i-1][cy1].blocker && !map[i+1][cy1].blocker && map[i][cy1-1].blocker && map[i][cy1+1].blocker) {
							map[i][cy1] = new Tile(Tile.DOOR, i, cy1);
						}
					}
				}
				
				// If cy1 > cy2, switch them so we only have to use one for statement
				if(cy1 > cy2) {
					int t = cy1;
					cy1 = cy2;
					cy2 = t;
				}
				
				for(int j = cy1; j < cy2; j ++) {
					if(map[cx2][j].blocker) {
						map[cx2][j] = new Tile(Tile.FLOOR, cx2, j);
						if(!map[cx2][j-1].blocker && !map[cx2][j+1].blocker && map[cx2-1][j].blocker && map[cx2+1][j].blocker) {
							map[cx2][j] = new Tile(Tile.DOOR, cx2, j);
						}
					}
				}
			}
		}
		else {
			// Rrrrrrrrrrrandom! (same deal)
			if(Math.random() > 0.5) { 
				for(int i = cx1; i < cx2; i ++) {
					if(map[i][cy2].blocker) {
						map[i][cy2] = new Tile(Tile.FLOOR, i, cy2);
						if(!map[i-1][cy2].blocker && !map[i+1][cy2].blocker && map[i][cy2-1].blocker && map[i][cy2+1].blocker) {
							map[i][cy2] = new Tile(Tile.DOOR, i, cy2);
						}
					}
				}
				
				// If cy1 > cy2, switch them so we only have to use one for statement
				if(cy1 > cy2) {
					int t = cy1;
					cy1 = cy2;
					cy2 = t;
				}
				
				for(int j = cy1; j < cy2; j ++) {
					if(map[cx1][j].blocker) {
						map[cx1][j] = new Tile(Tile.FLOOR, cx1, j);
						if(!map[cx1][j-1].blocker && !map[cx1][j+1].blocker && map[cx1-1][j].blocker && map[cx1+1][j].blocker) {
							map[cx1][j] = new Tile(Tile.DOOR, cx1, j);
						}
					}
				}
			}
			else { 
				for(int i = cx1; i < cx2; i ++) {
					if(map[i][cy1].blocker) {
						map[i][cy1] = new Tile(Tile.FLOOR, i, cy1);
						//   x
						// o + o
						//   x
						if(!map[i-1][cy1].blocker && !map[i+1][cy1].blocker && map[i][cy1-1].blocker && map[i][cy1+1].blocker) {
							map[i][cy1] = new Tile(Tile.DOOR, i, cy1);
						}
					}
				}
				
				// If cy1 > cy2, switch them so we only have to use one for statement
				if(cy1 > cy2) {
					int t = cy1;
					cy1 = cy2;
					cy2 = t;
				}
				
				for(int j = cy1; j < cy2; j ++) {
					if(map[cx2][j].blocker) {
						map[cx2][j] = new Tile(Tile.FLOOR, cx2, j);
						if(!map[cx2][j-1].blocker && !map[cx2][j+1].blocker && map[cx2-1][j].blocker && map[cx2+1][j].blocker) {
							map[cx2][j] = new Tile(Tile.DOOR, cx2, j);
						}
					}
				}
			}
		}
		
		
	}

	private void makeIntro(int width, int height, int difficulty) {
		//Create blank map
		map = new Tile[width][height];
		
		int CENTER = 17;
		int ROOM_MIN = CENTER-5;
		int ROOM_MAX = CENTER+5;
		
		// Create whole level (all floors)
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				map[i][j] = new Tile(Tile.FLOOR, i, j);
				if(i == 0 || j == 0 || i == map.length-1 || j == map[0].length-1) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
			}
		}
		
		// Make the corridor in the middle of the floor
		for(int i = ROOM_MIN; i <= ROOM_MAX; i++) {
			if(i == CENTER) {
				for(int j = CENTER+1; j < ROOM_MAX; j ++) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
			}
			else {
				for(int j = ROOM_MIN+1; j < ROOM_MAX; j ++) {
					if(j != CENTER || i > CENTER) {
						map[i][j] = new Tile(Tile.WALL, i, j);
					}
				}
			}
		}
		
		// Initial room
		for(int i = ROOM_MIN; i < map.length; i++) {
			for(int j = 0; j <= ROOM_MIN; j ++) {
				if(j == ROOM_MIN || i == ROOM_MIN) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
				map[i][j].room = 0;
			}
		}
		
		// Room with attack directions
		// And lots of little mushrooms
		for(int i = ROOM_MAX; i < map.length; i++) {
			for(int j = ROOM_MIN; j < map[0].length; j ++) {
				if(i == ROOM_MAX) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
				map[i][j].room = 1;
				
				// Add mushroom?
				if(i > ROOM_MAX && i < map.length - 1 && j > ROOM_MIN && j < map[0].length - 1) {
					if(Math.random() > 0.9) {
						Enemy e = new Enemy(i, j, Enemy.MUSHROOM);
						enemies.add(e);
					}
				}
			}
		}
		
		// Room with skills
		for(int i = 0; i < ROOM_MAX; i++) {
			for(int j = ROOM_MAX; j < map[0].length; j ++) {
				if(j == ROOM_MAX) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
				map[i][j].room = 2;
			}
		}
		
		//Add doors
		map[ROOM_MAX+5][ROOM_MIN] = new Tile(Tile.DOOR, ROOM_MAX+5, ROOM_MIN);
		map[ROOM_MAX][ROOM_MAX+5] = new Tile(Tile.DOOR, ROOM_MAX, ROOM_MAX + 5);
		map[ROOM_MIN-5][ROOM_MAX] = new Tile(Tile.DOOR, ROOM_MIN - 5, ROOM_MAX);
		map[ROOM_MIN][CENTER] = new Tile(Tile.DOOR, ROOM_MIN, CENTER);
		map[CENTER][ROOM_MIN] = new Tile(Tile.DOOR, CENTER, ROOM_MIN);
		
		map[(int)Math.floor(ROOM_MIN/2)][5] = new Tile(Tile.CHEST, (int)Math.floor(ROOM_MIN/2), 5);
		// Create boss mushroom
		Enemy e = new Enemy((int) (Math.random()*(ROOM_MIN-2)+1),(int) (Math.random()*(ROOM_MIN-2)+1), Enemy.ANGRY_MUSHROOM);
		e.setBounty((int)Math.floor(ROOM_MIN/2), 5, new Tile(Tile.CHEST));
		enemies.add(e);
	}

	private void makeCatacombs(int width, int height, int difficulty) {
		//Create blank map
		map = new Tile[width][height];

	}
	
	/*
	 * This Following part has nothing to do with specific levels but the metagame isn't
	 * important enough to have its own class.
	 * EVERYTHING METAGAME DOWN HERE (map generator)
	 */

	/**
	 * Bitwise values for the walls of the MetaMaze
	 */
	private static final int N = 1;
	private static final int E = 2;
	private static final int S = 4;
	private static final int W = 8;
	
	public static LevelTile[][] makeMetaGame(int width, int height) {
		int[][] maze = new int[width][height];

        // set all walls of each cell true in maze by setting NESW bits
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                maze[i][j] = (N + E + S + W);

        // create stack for storing previously visited locations
        int[][] stack = new int[width*height][2];

        // initialize stack
        for (int i = 0; i < width*height; i++)
            for (int j = 0; j < 2; j++)
            	stack[i][j] = 0;
        
        int[] curr = new int[2];
        Random rand = new Random();
        
        // arrays of single step movements between cells
        //              north    east     south    west    
        int[][] move = {{ 0,-1 }, { 1, 0 }, { 0, 1 }, {-1, 0 }};
        int[][] next = {{ 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 }};

        // choose a cell at random and make it the current cell
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
    
        // current search location
        curr[0] = x;  
        curr[1] = y;
        int visited  = 1;
        int total = width*height;
        int topOfStack   = 0;                              // index for top of cell stack
        
        while(visited < total) {
        	int j = 0;
        	for(int i = 0; i < 4; i ++) { 					// Find all neighbors and see if they've been visited
                x = curr[0] + move[i][0];
                y = curr[1] + move[i][1];

                //  check for valid next cell
                if ((0 <= x) && (x < width) && (0 <= y) && (y < height))
                {
                    // check if previously visited
                    if (((maze[x][y] & N) == N) && ((maze[x][y] & E)==E) && ((maze[x][y] & S)==S) && ((maze[x][y] & W)==W))
                    {
                        // not visited, so add to possible next cells
                        next[j][0] = x;
                        next[j][1] = y;
                        j++;
                    }
                }
        	}
        	
        	if(j > 0) {				// Move to neighbor
        		int i = rand.nextInt(j);

                if ((next[i][0] - curr[0]) == 0)    // next on same column
                {
                    x = next[i][0];
                    if (next[i][1] > curr[1])       // move east
                    {
                        y = curr[1];
                        maze[x][y] &= ~S;           // clear E wall
                        y = next[i][1];
                        maze[x][y] &= ~N;           // clear W wall
                    }
                    else                            // move west
                    {
                        y = curr[1];
                        maze[x][y] &= ~N;           // clear W wall
                        y = next[i][1];
                        maze[x][y] &= ~S;           // clear E wall
                    }
                }
                else                                // next on same row
                {
                    y = next[i][1];
                    if (next[i][0] > curr[0])       // move south    
                    {
                        x = curr[0];
                        maze[x][y] &= ~E;           // clear S wall
                        x = next[i][0];
                        maze[x][y] &= ~W;           // clear N wall
                    }
                    else                            // move north
                    {
                        x = curr[0];
                        maze[x][y] &= ~W;           // clear N wall
                        x = next[i][0];
                        maze[x][y] &= ~E;           // clear S wall
                    }
                }

                topOfStack++;                              // push current cell location
                stack[topOfStack][0] = curr[0];
                stack[topOfStack][1] = curr[1];

                curr[0] = next[i][0];               // make next cell the current cell
                curr[1] = next[i][1];

                visited++;                          // increment count of visited cells
        	}
        	else {					// Dead end, start backtracking
                // pop the most recent cell from the cell stack            
                // and make it the current cell
                curr[0] = stack[topOfStack][0];
                curr[1] = stack[topOfStack][1];
                topOfStack--;
        	}
        }
        
        LevelTile[][] map = new LevelTile[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
            	int m = maze[i][j];
            	boolean[] walls = { (m & N) == N, (m & E) == E, (m & S) == S, (m & W) == W };
            	map[i][j] = new LevelTile(rand.nextInt(2)+2, walls);
            }
        }
        return map;
	}
	
	/** Creates a tile and puts it in the map. No more silly mistakes! */
	public void newTile(int type, int id, int x, int y, Tile[][] currMap) {
		Tile t = new Tile(type, id, x, y);
		currMap[x][y] = t;
	}
	
	public void newTile(int type, int x, int y, Tile[][] currMap) {
		Tile t = new Tile(type, x, y);
		currMap[x][y] = t;
	}
}
