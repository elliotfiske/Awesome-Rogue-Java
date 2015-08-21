/****
 * I AM SUCH A GOOD PRORAMMER HNNNNNNGGHH
 */

package com.nightfall.awesomerogue;

import java.awt.Point;
import java.util.ArrayList;

public class TestCasesAreFun {

	//Handy dandy map-man
	static Tile f = new Tile(Tile.FLOOR);
	static Tile w = new Tile(Tile.WALL);
	static Tile[][] map = 
		{{w,w,w,w,w,w},
		{w,f,f,f,f,w},
		{w,f,w,f,f,w},
		{w,f,w,f,w,w},
		{w,f,w,f,f,w},
		{w,w,w,w,w,w}};

	/*
	 * 
 	      W      W      W      W      W      W     
 	      W      F      F      F      F      W     
 	      W      F      W      W      W      W     
	      W      F      F      F      F      W     
	      W      F      F      W      F      W     
	      W      W      W      W      W      W  

		(0,0)  (1,0)  (2,0)  (3,0)  (4,0)  (5,0)  
		(0,1)  (1,1)  (2,1)  (3,1)  (4,1)  (5,1)  
		(0,2)  (1,2)  (2,2)  (3,2)  (4,2)  (5,2)  
		(0,3)  (1,3)  (2,3)  (3,3)  (4,3)  (5,3)  
		(0,4)  (1,4)  (2,4)  (3,4)  (4,4)  (5,4)  
		(0,5)  (1,5)  (2,5)  (3,5)  (4,5)  (5,5)  


	 */

	//TEST IF THE PARTICLE SYSTEM MAKES ANY FRIGGIN SENSE
	public static void main(String[] args) {
		int[] straightPoints = {-1,-2, 0,-2, 1,-2, 0,-1};
		int[] diagonalPoints = {1,-1, 1,-2, 2,-1};
		
		/*
		//Tester bo bester
		for(Point point : result) {
			System.out.println(point.toString());
		}
		*/
		boolean pass = true;
		
		//Direction 0
		Point[] expectedResult = {new Point(-1, -2), new Point(0, -2), new Point(1, -2), new Point(0, -1)};
		Point[] result = Utility.makePointArray(straightPoints, 0);
		pass &= assertEquals(result, expectedResult, 0);
		
		//Direction 1
		expectedResult = new Point[]{new Point(1, -1), new Point(1, -2), new Point(2, -1)};
		result = Utility.makePointArray(diagonalPoints, 1);
		pass &= assertEquals(result, expectedResult, 1);
		
		//Direction 2
		expectedResult = new Point[]{new Point(2, -1), new Point(2, 0), new Point(2, 1), new Point(1, 0)};
		result = Utility.makePointArray(straightPoints, 2);
		pass &= assertEquals(result, expectedResult, 2);
		
		//Direction 3
		expectedResult = new Point[]{new Point(1, 1), new Point(1, 2), new Point(2, 1)};
		result = Utility.makePointArray(diagonalPoints, 3);
		pass &= assertEquals(result, expectedResult, 3);
		
		//Direction 4
		expectedResult = new Point[]{new Point(-1, 2), new Point(0, 2), new Point(1, 2), new Point(0, 1)};
		result = Utility.makePointArray(straightPoints, 4);
		pass &= assertEquals(result, expectedResult, 4);
		
		//Direction 5
		expectedResult = new Point[]{new Point(-1, 1), new Point(-1, 2), new Point(-2, 1)};
		result = Utility.makePointArray(diagonalPoints, 5);
		pass &= assertEquals(result, expectedResult, 5);
		
		//Direction 6
		expectedResult = new Point[]{new Point(-2, -1), new Point(-2, 0), new Point(-2, 1), new Point(-1, 0)};
		result = Utility.makePointArray(straightPoints, 6);
		pass &= assertEquals(result, expectedResult, 6);
		
		//Direction 7
		expectedResult = new Point[]{new Point(-1, -1), new Point(-1, -2), new Point(-2, -1)};
		result = Utility.makePointArray(diagonalPoints, 7);
		pass &= assertEquals(result, expectedResult, 7);

		if(pass) {
			System.out.println("hooray you did it whoo");
		}
	}

	//TEST IF THE ENEMIES CAN WALK IN A STRAIGHT LINE K
	public static void testEnemyStraightLine() {
		Enemy e = new Enemy(0, 0, 0);
		Utility.walkStraight(new Point(25, 25), new Point(20, 20), 3);
		//assertEquals(new Point(-1, -1),);
		Utility.walkStraight(new Point(25, 15), new Point(20, 20), 3);
		//assertEquals(new Point(-1, 1), );
	}

	//TEST IF ENEMIES CAN FOLLOW A WALL LIKE A CLEVER LITTLE ENEMY
	private static void testEnemyWallFollow() { 
		//7 0 1
		//6   2
		//5 4 3
		boolean pass = true;

		Enemy e = new Enemy(0, 0, 0);

		Point leftFeeler = new Point(3,1);
		Point lastWall = new Point(3,0);
		//lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
		pass &= assertEquals(new Point(2,1), leftFeeler);
		pass &= assertEquals(new Point(2,0), lastWall);

		//lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
		pass &= assertEquals(new Point(1,1), leftFeeler);
		pass &= assertEquals(new Point(1,0), lastWall);

		//lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
		pass &= assertEquals(new Point(1,2), leftFeeler);
		pass &= assertEquals(new Point(0,2), lastWall);

		/*Point feeler = new Point(4, 4);
		Point lastWall = e.getDirection(feeler, new Point(4, 5), true, map);
		pass &= assertEquals(new Point(3, 4), lastWall);
		pass &= assertEquals(new Point(3, 3), feeler);

		lastWall = e.getDirection(feeler, lastWall, true, map);
		pass &= assertEquals(new Point(2, 4), feeler);
		pass &= assertEquals(new Point(3, 4), lastWall);

		lastWall = e.getDirection(feeler, lastWall, true, map);
		pass &= assertEquals(new Point(1, 4), feeler);
		pass &= assertEquals(new Point(1, 5), lastWall);

		lastWall = e.getDirection(feeler, lastWall, true, map);
		pass &= assertEquals(new Point(1, 3), feeler);
		pass &= assertEquals(new Point(0, 3), lastWall);*/

		if(pass) {
			System.out.println("hooray you did it whoo");
		}

	}


	//assertions of equality

	public static boolean assertEquals(Point expected, Point actual) {
		if(expected.x == actual.x && expected.y == actual.y) {
			return true;
		}

		System.out.println("Expected: (" + expected.x + ", " + expected.y + ")  Got: (" + actual.x +", " + actual.y + ")");
		return false;
	}

	public static boolean assertEquals(int expected, int actual) {
		if(expected == actual) {
			return true;
		}

		System.out.println("Expected: " + expected + ",  Got: " + actual);
		return false;
	}
	
	private static boolean assertEquals(Point[] result, Point[] expectedResult, int testNum) {
		if(result.length != expectedResult.length) {
			System.out.println("Length of result: " + result.length + 
					", length of expected: " + expectedResult.length);
			return false;
		}
		
		for(int i = 0; i < result.length; i++) {
			if(result[i].x != expectedResult[i].x || result[i].y != expectedResult[i].y) {
				System.out.println("Mismatch at " + i + ": GOT " + result[i] + " EXPECTED "
						+ expectedResult[i] + " on test #" + testNum);
				return false;
			}
		}
		
		return true;
	}

	public static void generateGrids() {
		//lol
		//		for(int x = 0; x < 6; x++) {
		//			for(int y = 0; y < 6; y++) {
		//				System.out.print("("+y+","+x+")  ");
		//			}
		//			System.out.println();
		//		}

		printMap(0,0);
		printMap(1,0);
		printMap(2,0);
		printMap(3,0);
		printMap(4,0);
		printMap(5,0);
		System.out.println();
		printMap(0,1);
		printMap(1,1);
		printMap(2,1);
		printMap(3,1);
		printMap(4,1);
		printMap(5,1);
		System.out.println();
		printMap(0,2);
		printMap(1,2);
		printMap(2,2);
		printMap(3,2);
		printMap(4,2);
		printMap(5,2);
		System.out.println();
		printMap(0,3);
		printMap(1,3);
		printMap(2,3);
		printMap(3,3);
		printMap(4,3);
		printMap(5,3);
		System.out.println();
		printMap(0,4);
		printMap(1,4);
		printMap(2,4);
		printMap(3,4);
		printMap(4,4);
		printMap(5,4);
	}

	public static void printMap(int x, int y) {
		if(map[x][y].type == Tile.FLOOR) {
			System.out.print("F ");
		} else {
			System.out.print("W ");
		}
	}
}
