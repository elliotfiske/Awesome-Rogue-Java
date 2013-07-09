/****
 * I AM SUCH A GOOD PRORAMMER HNNNNNNGGHH
 */

package com.nightfall.awesomerogue;

import java.awt.Point;

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

	public static void main(String[] args) {

		Enemy e = new Enemy(0, 0, Enemy.RAT);
		System.out.println("classy: " + e.toString());
		//testEnemyStraightLine();
		//testEnemyWallFollow();
		//		generateGrids();
	}

	//TEST IF THE ENEMIES CAN WALK IN A STRAIGHT LINE K
	public static void testEnemyStraightLine() {
		Enemy e = new Enemy(0, 0, 0);
		e.walkStraight(new Point(25, 25), new Point(20, 20), 3);
		//assertEquals(new Point(-1, -1),);
		e.walkStraight(new Point(25, 15), new Point(20, 20), 3);
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
		lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
		pass &= assertEquals(new Point(2,1), leftFeeler);
		pass &= assertEquals(new Point(2,0), lastWall);

		lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
		pass &= assertEquals(new Point(1,1), leftFeeler);
		pass &= assertEquals(new Point(1,0), lastWall);

		lastWall = e.getDirection(leftFeeler, lastWall, false, map, null);
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
