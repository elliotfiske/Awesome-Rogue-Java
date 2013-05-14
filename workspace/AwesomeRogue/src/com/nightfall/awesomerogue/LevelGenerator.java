package com.nightfall.awesomerogue;

import java.util.Random;

public class LevelGenerator {
	
	public static final int CAVE = 0;
	public static final int ROOMS = 1;
	public static final int CATACOMBS = 2;

	private static final int SEED = 37;
	/**
	 * For the Cave level.  How many walls should there be adjacent to a tile for it to
	 * become a wall itself?
	 */
	private static final int SMOOTHNESS = 3;
	/**
	 * How many times should we Conway-ify the cave level?
	 */
	private static final int CAVE_ITERATIONS = 6;

	/**
	 * Generate a new level with the specified parameters.
	 * 
	 * NOTE: this method MODIFIES the actual array of Tiles you pass in, since
	 * Java makes arrays a shallow copy.
	 * 
	 * @param map The array you want to have a level in it.
	 * @param type LevelGenerator.CAVE, LevelGenerator.ROOMS, LevelGenerator.CATACOMBS
	 * @param width Width of the map
	 * @param height Height of the map.
	 * @param currState A handle to the InGameState so we can access Tiles :/
	 */
	public void makeLevel(Tile[][] map, int type, int width, int height) {
		switch(type) {
		case CAVE:
			makeCaves(map, width, height);
			break;
		case ROOMS:
			makeRooms(map, width, height);
			break;
		case CATACOMBS:
			makeCatacombs(map, width, height);
			break;
		}
	}

	private void makeCaves(Tile[][] map, int width, int height) {
		//Make our sweet number generator
		Random numGen = new Random(SEED);

		//Representational.  Stores whether the specified tile is a floor or a wall (0 or 1 respectively).
		int[][] numMap = new int[width][height];

		//First, start with random noise
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				numMap[x][y] = numGen.nextInt(2);
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
								if(dx != 0 && dy != 0) {

									if(numMap[x + dx][y + dy] == 1 ) {
										adjacentWalls++;
									}

								}
							} catch(ArrayIndexOutOfBoundsException e) { /* It happens.  We forgive you. */ }
						}
					}

					//adjacentWalls now contains the number of walls adjacent to the cell we're looking at.
					if(adjacentWalls >= SMOOTHNESS - 1 && numMap[x][y] == 1) {
						tempMap[x][y] = 1;
						System.out.println("LOLOLOL WALLZ");
					} else if(adjacentWalls >= SMOOTHNESS && numMap[x][y] == 0) {	
						tempMap[x][y] = 1;
						System.out.println("LOLOLOL WALLZ");
					} else {
						tempMap[x][y] = 0;
					}
				}
			}
			
			//Copy the tempMap to the numMap
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					numMap[x][y] = tempMap[x][y];
					//System.out.print(numMap[x][y]);
				}
				//System.out.println();
			}
			
		} //Here ends one iteration.
		
		//Convert the map of 0's and 1's to floors and walls.
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(numMap[x][y] == 0) {
					map[x][y] = new Tile(Tile.FLOOR);
				}
				
				if(numMap[x][y] == 1) {
					map[x][y] = new Tile(Tile.WALL);
				}
			}
		}
		
	}

	private static void makeRooms(Tile[][] map, int width, int height) {

	}

	private static void makeCatacombs(Tile[][] map, int width, int height) {

	}
}
