package com.nightfall.awesomerogue;

/****
 * Has a bunch of handy static methods. I'm gonna move the pathfinding algorithm here in a bit.
 */
public class Utility {

	public static int sign(int i) {
		if(i < 0) return -1;
		if(i > 0) return 1;
		return 0;
	}
}
