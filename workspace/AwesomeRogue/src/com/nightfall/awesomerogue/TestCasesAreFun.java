/****
 * I AM SUCH A GOOD PRORAMMER HNNNNNNGGHH
 */

package com.nightfall.awesomerogue;

public class TestCasesAreFun {

	public static void main(String[] args) {
		testEnemyStraightLine();
	}
	
	public static void testEnemyStraightLine() {
		Enemy e = new Enemy(0, 0, 0);
		
		System.out.println("(-1, -1): " + e.walkStraight(5, 5, 0, 0));
		
		System.out.println("(-1, 0): " + e.walkStraight(2, 1, 0, 0));
		System.out.println("(-1, 0): " + e.walkStraight(5, 2, 0, 0));
		
		System.out.println("(1,1): " + e.walkStraight(-1, -1, 0, 0));
		System.out.println("(1,1): " + e.walkStraight(-5, -4, 0, 0));
		
		System.out.println("(0,1): " + e.walkStraight(-1, 5, 0, 0));
		
		System.out.println("(1,1): " + e.walkStraight(-6, 4, 0, 0));
	}
	

}
