package com.nightfall.awesomerogue;

import java.util.ArrayList;
import java.util.Random;

public class LevelGenerator {

	public static final int CAVE = 3;
	public static final int INTRO = 0;
	public static final int ROOMS = 1;
	public static final int CATACOMBS = 2;

	/**
	 * How hard it gonn be like?
	 */
	private int difficulty;
	
	private static final int SEED = 15654;

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
	 * Bitwise values for the walls of the MetaMaze
	 */
	private static final int N = 1;
	private static final int E = 2;
	private static final int S = 4;
	private static final int W = 8;

	/**
	 * Generate a new level with the specified parameters.
	 * 
	 * Also populates it with scary spooky monsters.
	 * 
	 * NOTE: this method MODIFIES the actual array of Tiles you pass in, since
	 * Java makes arrays a shallow copy.
	 * 
	 * @param map The array you want to have a level in it.
	 * @param type LevelGenerator.CAVE, LevelGenerator.ROOMS, LevelGenerator.CATACOMBS
	 * @param width Width of the map.
	 * @param height Height of the map.
	 * @param currState A handle to the InGameState so we can access Tiles :/
	 * @param difficulty How hard do I want it?
	 */
	public void makeLevel(Tile[][] map, int type, int width, int height, int difficulty) {
		switch(type) {
		case CAVE:
			makeCaves(map, width, height, difficulty);
			break;
		case ROOMS:
			//makeRooms(map, width, height);
			break;
		case INTRO:
			//makeIntro(map, width, height);
			break;
		case CATACOMBS:
			makeCatacombs(map, width, height);
			break;
		}
	}
	
	/**
	 * Generate a new level with the specified parameters - Thomas' Way
	 * 
	 * Also populates it with scary spooky monsters.
	 * 
	 * NOTE: this method CREATES the map once and passes it. Sure it's shallow but it
	 * doesn't matter because this method sure won't be doing anything with it later.
	 * 
	 * @param type LevelGenerator.CAVE, LevelGenerator.ROOMS, LevelGenerator.CATACOMBS
	 * @param width Width of the map.
	 * @param height Height of the map.
	 * @param currState A handle to the InGameState so we can access Tiles :/
	 * @param difficulty How hard do I want it?
	 */
	public static LevelInfo makeLevel(int type, int width, int height, int difficulty) {
		switch(type) {
		case CAVE:
			return makeCaves(width, height, difficulty);
		case ROOMS:
			//return makeRooms(width, height);
			break;
		case INTRO:
			//return makeIntro(width, height);
		case CATACOMBS:
			//return makeCatacombs(width, height);
			break;
		}
		return null;
	}

	private void makeCaves(Tile[][] map, int width, int height, int difficulty) {
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
		
		
		//Convert the map of 0's and 1's to floors and walls.
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == 0) {
					//tempMap still has the ID's in it.
					//For debugging I want to print out the tile #'s.
					map[x][y] = new Tile(Tile.FLOOR, (tempMap[x][y]), x, y);
				}

				if(numMap[x][y] == 1) {
					map[x][y] = new Tile(Tile.WALL, 0, x, y);
				}
			}
		}
		
		//Make sure you CAN NEVER ESCAPE
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(x == 0 || x == width - 1) {
					map[x][y] = new Tile(Tile.IMPASSABLE, x, y);
				}
				
				if(y == 0 || y == height - 1) {
					map[x][y] = new Tile(Tile.IMPASSABLE, x, y);
				}
			}
		}
		
		//Plop down some enemies based on the difficulty.
		if(difficulty == 1) {
			Enemy bob = new Enemy(25, 10, Enemy.RAT);
			InGameState.enemies.add(bob);
			
			Enemy joe = new Enemy(10, 25, Enemy.MUSHROOM);
			InGameState.enemies.add(joe);
		}
		
		if(difficulty == 2) {
			//Divide the map into squares of size 10x10 and plop down 3 monsters each.
			for(int xTen = 0; xTen < width; xTen += 10) {
				for(int yTen = 0; yTen < height; yTen += 10) {
					
					for(int monsterNum = 0; monsterNum < 3; monsterNum++) {
						int monsterX = numGen.nextInt(10) + xTen;
						int monsterY = numGen.nextInt(10) + yTen;
						
						//Choose a type
						int type = 0;
						switch(numGen.nextInt(2)) {
						case 0:
							type = Enemy.ANGRY_MUSHROOM;
							break;
						case 1:
							type = Enemy.RAT;
							break;
						}
						
						//if it's on a wall, it's outta luck.  So sad, try again next time.
						if(map[monsterX][monsterY].type != Tile.FLOOR) {
							continue;
						}
						
						Enemy newEnemy = new Enemy(monsterX, monsterY, type);
						InGameState.enemies.add(newEnemy);
						
					}
				
				}
			}
		}

	}

	private static LevelInfo makeCaves(int width, int height, int difficulty) {
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
		
		Tile[][] map = new Tile[width][height];
		//Convert the map of 0's and 1's to floors and walls.
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == 0) {
					//tempMap still has the ID's in it.
					//For debugging I want to print out the tile #'s.
					map[x][y] = new Tile(Tile.FLOOR, (tempMap[x][y]), x, y);
				}

				if(numMap[x][y] == 1) {
					map[x][y] = new Tile(Tile.WALL, 0, x, y);
				}
			}
		}
		
		//Make sure you CAN NEVER ESCAPE
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(x == 0 || x == width - 1) {
					map[x][y] = new Tile(Tile.IMPASSABLE, x, y);
				}
				
				if(y == 0 || y == height - 1) {
					map[x][y] = new Tile(Tile.IMPASSABLE, x, y);
				}
			}
		}
		
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		//Plop down some enemies based on the difficulty.
		if(difficulty == 1) {
			Enemy bob = new Enemy(25, 10, Enemy.RAT);
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
						int type = 0;
						switch(numGen.nextInt(2)) {
						case 0:
							type = Enemy.ANGRY_MUSHROOM;
							break;
						case 1:
							type = Enemy.RAT;
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

		return new LevelInfo(map, enemies);
	}

	private static void makeRooms(Tile[][] map, int width, int height, ArrayList<Character> enemies) {
		
	}

	public static Tile[][] makeIntro(int width, int height, ArrayList<Enemy> enemies) {
		int CENTER = 17;
		int ROOM_MIN = CENTER-5;
		int ROOM_MAX = CENTER+5;
		Tile[][] map = new Tile[38][35];
		
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
		
		return map;
	}

	private static void makeCatacombs(Tile[][] map, int width, int height) {

	}
	
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
    
}
