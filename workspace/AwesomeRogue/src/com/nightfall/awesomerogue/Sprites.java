/****
 * Class to manage sprites and stuff
 */

package com.nightfall.awesomerogue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprites {
	
	/** Every enemy! */
	public static BufferedImage[] enemyImages;
	
	/** For the Drill Dozer, widening cracks innawall. */
	public static BufferedImage[] cracks;
	
	
	public static void loadSprites() {
		
		//Add the BufferedImage to the Enemy Data enum
		for(Enemy.Data enemyData : Enemy.Data.values()) {
			try {
				enemyData.image = ImageIO.read(new File("img/" + enemyData.image));
			} catch (IOException e) {
				System.out.println("Couldn't find " + enemyData.name + " image!");
			}
		}
		
		cracks = GameFrame.loadAnimation("BreakBlox.png", 16);
	}
	
}
