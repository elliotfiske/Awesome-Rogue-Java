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

	/** Every pet! */
	public static BufferedImage[] petImages;
	
	/** For the Drill Dozer, widening cracks innawall. */
	public static BufferedImage[] cracks;


	public static void loadSprites() {
		enemyImages = new BufferedImage[Enemy.WIZARD + 1];
		petImages = new BufferedImage[2];
		
		loadEnemies();
		loadPets();

		cracks = GameFrame.loadAnimation("BreakBlox.png", 16);
	}

	private static void loadEnemies() {

		//Load each and every enemy image
		try {
			enemyImages[Enemy.MUSHROOM] = ImageIO.read(new File("img/enemies/mushroom.png"));
			enemyImages[Enemy.RAT] = ImageIO.read(new File("img/enemies/rat.png"));
			enemyImages[Enemy.ZOMBIE] = ImageIO.read(new File("img/enemies/zombie.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadPets() {
		try {
			petImages[Pet.DRILL_DOZER] = ImageIO.read(new File("img/pets/grenade.png"));
			petImages[Pet.GRENADE] = ImageIO.read(new File("img/pets/grenade.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
