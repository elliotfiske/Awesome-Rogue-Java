/****
 * Class to manage sprites and stuff
 */

package com.nightfall.awesomerogue;

import java.awt.image.BufferedImage;

public class Sprites {
	
	/** For the Drill Dozer, widening cracks innawall. */
	public static BufferedImage[] cracks;
	
	public static void loadSprites() {
		cracks = GameFrame.loadAnimation("BreakBlox.png", 16);
	}
	
}
