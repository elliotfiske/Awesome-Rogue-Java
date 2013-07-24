package com.nightfall.awesomerogue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class LevelInfo {
  public static final int CAVE = 3;
	public static final int INTRO = 0;
	public static final int BOSS = 1;
	public static final int ROOMS = 2;
	public static final int CATACOMBS = 4;
	public static final int RUINS = 5;
	
	private static final int SEED = 20;

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
	
	private Tile[][] map;
	private ArrayList<Character> enemies;
	private Point startPos;

	/**
	 * How hard it gonn be like?
	 */
	private int difficulty;
	
	public LevelInfo(int type, int difficulty) {
		int width, height;
		
		enemies = new ArrayList<Character>();
		switch(type) {
		case CAVE:
			width = 80;
			height = 70;

			makeCaves(width, height, difficulty);
			startPos = new Point(53, 60);
			break;
		case ROOMS:
			width = 80;
			height = 60;

			makeRooms(width, height, difficulty);
			startPos = new Point(40, 30);
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
		System.out.println(type);
		
		this.difficulty = difficulty;
	}
	
	public LevelInfo(Tile[][] map, ArrayList<Character> enemies) {
		this.map = map;
		this.enemies = enemies;
	}
	
	public Tile[][] getMap() {
		return map;
	}
	
	public ArrayList<Character> getEnemies() {
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

	}
	
	private void makeRooms(int width, int height, int difficulty) {
		//Create blank map
		map = new Tile[width][height];

		ArrayList<Point> roomCenters = new ArrayList<Point>();
		
		// Create whole level (all walls)
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
		
		// Make a really long snaking corridor first
		// Rooms will be added at corners
		
		
		// Four options:
		//      |  *
		//  *-> |  v
		// -----|-----
		//  ^   |
		//  *   | <-*
		// Starting direction is either 1, 2, 4, 8 (N, E, S, W) 
		int direction = (int) Math.pow(2, Math.floor(Math.random()*4));
		
		Point cursor = new Point(0, 0);
		switch(direction) {
		case N:
			cursor = new Point((int) (Math.random()*width/2), (int) (Math.random()*height/2) + height/2);
			break;
		case E:
			cursor = new Point((int) (Math.random()*width/2), (int) (Math.random()*height/2));
			break;
		case S:
			cursor = new Point((int) (Math.random()*width/2)+width/2, (int) (Math.random()*height/2));
			break;
		case W:
			cursor = new Point((int) (Math.random()*width/2)+width/2, (int) (Math.random()*height/2)+height/2);
			break;
		}
		roomCenters.add(cursor.getLocation());
		startPos = cursor.getLocation();
		
		for(int roomNum = 0; roomNum < 10; roomNum ++) {
			// Add 10 to compensate for room sizes
			int pipelen = (int) (Math.random() * 20 + 20);
			switch(direction) {
			case N:
				if(cursor.y - pipelen < 0) pipelen = cursor.y;
				// Treate pipelen as a countdown, but move the cursor itself
				while(pipelen -- > 0) {
					map[cursor.x][cursor.y] = new Tile(Tile.FLOOR, cursor.x, cursor.y);
					cursor.y --;
				}
				break;
			case E:
				if(cursor.x + pipelen > map.length - 1) pipelen = (map.length - 1) - cursor.x;
				// Treate pipelen as a countdown, but move the cursor itself
				while(pipelen -- > 0) {
					map[cursor.x][cursor.y] = new Tile(Tile.FLOOR, cursor.x, cursor.y);
					cursor.x ++;
				}
				break;
			case S:
				if(cursor.y + pipelen > map[0].length - 1) pipelen = (map[0].length - 1) - cursor.y;
				// Treate pipelen as a countdown, but move the cursor itself
				while(pipelen -- > 0) {
					map[cursor.x][cursor.y] = new Tile(Tile.FLOOR, cursor.x, cursor.y);
					cursor.y ++;
				}
				break;
			case W:
				if(cursor.x - pipelen < 0) pipelen = cursor.x;
				// Treate pipelen as a countdown, but move the cursor itself
				while(pipelen -- > 0) {
					map[cursor.x][cursor.y] = new Tile(Tile.FLOOR, cursor.x, cursor.y);
					cursor.x --;
				}
				break;
			}

			if(Math.random() > 0.5)
				direction *= 2;
			else
				direction /= 2;
			if(direction > W) direction = N;
			if(direction < N) direction = W;
		}
		roomCenters.add(cursor.getLocation());
		
		// DOOOOOOOOOOOOOORS
		// Check to see if any corridor spot touches 2 floor tiles
		// non-linearly (aka corner or intersection, and if so
		// create a room!
		
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				if(map[i][j].type != Tile.FLOOR) continue;
				// Adjactent horizontal and vertical floors
				boolean horiz = false, vert = false;
				
				if((i > 0 && map[i-1][j].type == Tile.FLOOR) || (i < map.length-1 && map[i+1][j].type == Tile.FLOOR)) {
					horiz = true;
				}
				if((j > 0 && map[i][j-1].type == Tile.FLOOR) || (j < map[0].length-1 && map[i][j+1].type == Tile.FLOOR)) {
					vert = true;
				}
				
				if(horiz && vert) {
					roomCenters.add(new Point(i, j));
				}
			}
		}
		
		// Now actually make the rooms
		for(Point center : roomCenters) {
			makeRoom(center.x - (int) (Math.random() * 4 + 4), center.y - (int) (Math.random() * 4 + 4),
					center.x + (int) (Math.random() * 4 + 4), center.y + (int) (Math.random() * 4 + 4));
		}
		
		// Spot checks! Sometimes we get long dead end corridors or walls
		// Remove those here
		// Also, do one more door run-through
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				if(map[i][j].type == Tile.WALL) checkForDoor(i, j);
			}
		}
	}
	
	/**
	 * Make a generic room (just walls and floor for now)
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 */
	private void makeRoom(int minx, int miny, int maxx, int maxy) {
		// Don't wanna go out of bounds!
		if(minx < 0) minx = 0;
		if(miny < 0) miny = 0;
		if(maxx >= map.length) maxx = map.length - 1;
		if(maxy >= map[0].length) maxy = map[0].length - 1;
		
		// Make the room
		for(int i = minx; i <= maxx; i ++) {
			for(int j = miny; j <= maxy; j ++) {
//				if(i == minx || i == maxx-1 || j == miny || j == maxy - 1) {
//					map[i][j] = new Tile(Tile.WALL, i, j);
//				}
//				else {
				/*if((i == minx && i > 0 && !map[i-1][j].blocker) ||
						(i == maxx && i < map.length-1 && !map[i+1][j].blocker) ||
						(j == miny && j > 0 && !map[i][j-1].blocker) ||
						(j == maxy && j < map[0].length-1 && !map[i][j+1].blocker)) {
					int walls = 0;

					// Check and make sure its next to some walls (otherwise we get a bunch of doors)
					for(int di = -1; di <= 1; di ++) {
						for(int dj = -1; dj <= 1; dj ++) {
							// No doors on the edge of the map!!
							if(i + di <= 0 || i + di >= map.length - 1) {
								walls ++;
								continue;
							}
							if(j + dj <= 0 || j + dj >= map[0].length - 1) {
								walls ++;
								continue;
							}
							
							if(map[i+di][j+dj].type == Tile.WALL) {
								walls ++;
							}
						}
					}
					
					if(walls >= 4)
						map[i][j] = new Tile(Tile.DOOR, i, j);
				}
				else */ 
				if((i == minx && i > 0) || (i == maxx && i < map.length - 1) || 
						(j == miny && j > 0) || (j == maxy && j < map[0].length - 1)) {
					map[i][j] = new Tile(Tile.WALL, i, j);
				}
				else {
					map[i][j] = new Tile(Tile.FLOOR, i, j);
				}
				
//				}
				
			}
		}
		
		// Now let's add doors!
		if(miny > 0) {
			for(int x = minx; x < maxx; x ++) {
				checkForDoor(x, miny);
			}
		}
		if(maxy < map[0].length-1) {
			for(int x = minx; x < maxx; x ++) {
				checkForDoor(x, maxy);
			}
		}
		if(minx > 0) {
			for(int y = miny; y < maxy; y ++) {
				checkForDoor(minx, y);
			}
		}
		if(maxx < map.length-1) {
			for(int y = miny; y < maxy; y ++) {
				checkForDoor(maxx, y);
			}
		}
	}
	
	private void checkForDoor(int i, int j) {
		int walls = 0;
		int floors = 0;
		boolean doorNeighbor = false;

		// Check and make sure its next to some walls (otherwise we get a bunch of doors)
		for(int di = -1; di <= 1; di ++) {
			for(int dj = -1; dj <= 1; dj ++) {
				// Ignore myself
				if(di == 0 && dj == 0) {
					
				}
				// Check straight left, up, down, right
				// If there are two or more floors, we have an intersection!
				else if(di == 0 || dj == 0) {
					// No doors on the edge of the map!!
					if(i + di <= 0 || i + di >= map.length - 1 || j + dj <= 0 || j + dj >= map[0].length - 1) {
						continue;
					}

					if(!map[i+di][j+dj].blocker) {
						floors ++;
					}
					
					if(map[i+di][j+dj].type == Tile.DOOR) // FLOOR THAT BIT...Of the map
						doorNeighbor = true;
				}
				// Check diagonals
				// If we have two or more walls, there is a doorway!
				else {
					// No doors on the edge of the map!!
					if(i + di <= 0 || i + di >= map.length - 1) {
						walls ++;
						continue;
					}
					if(j + dj <= 0 || j + dj >= map[0].length - 1) {
						walls ++;
						continue;
					}
					
					if(map[i+di][j+dj].type == Tile.WALL) {
						walls ++;
					}
				}
			}
		}
		
		if((walls >= 2 && floors >= 2) || (floors == 1 && doorNeighbor && walls >= 2))
			map[i][j] = new Tile(Tile.DOOR, i, j);
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
				//System.out.println("WE ARE A GO HOUSTON");
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
}
