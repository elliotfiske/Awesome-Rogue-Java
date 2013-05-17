package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class MetaGameState extends GameState {
	public static final int INGAME_WINDOW_OFFSET_X = 32;	// In pixels, not cells
	public static final int INGAME_WINDOW_OFFSET_Y = 23;	// In pixels, not cells
	public static final int LEVEL_ANIM_OFFSET_X = 551;	// In pixels, not cells
	public static final int LEVEL_ANIM_OFFSET_Y = 23;	// In pixels, not cells
	public static final int LEVEL_INFO_OFFSET_X = 551;	// In pixels, not cells
	public static final int LEVEL_INFO_OFFSET_Y = 180;	// In pixels, not cells
	public static final int CELL_SIZE = 80;
	public static final int META_MAP_WIDTH = 6;				// In cells, not pixels
	public static final int META_MAP_HEIGHT = 6;			// In cells, not pixels

	/**
	 * Describes the cell location of the upper lefthand corner of the area
	 * we render.
	 */
	public LevelTile[][] map;

	private int charX, charY;
	private BufferedImage metaChar;
	private Character mainChar;
	
	private int wizX, wizY;
	private BufferedImage wizChar;

	private ImageSFX imgSFX;

	private BufferedImage[] guiBG;
	private LevelAnim[] levelAnim;

	private BufferedImage[] tileImages;

	public static ArrayList<Enemy> enemies;

	public MetaGameState(GamePanel gameCanvas) throws IOException {
		super(gameCanvas);

		imgSFX = new ImageSFX();

		tileImages = new BufferedImage[10];
		tileImages[0] = ImageIO.read(new File("img/metagame/IntroMetaTile.png"));
		tileImages[1] = ImageIO.read(new File("img/metagame/WizardMetaTile.png"));
		tileImages[2] = ImageIO.read(new File("img/metagame/RoomMetaTile.png"));
		tileImages[3] = ImageIO.read(new File("img/metagame/CaveMetaTile.png"));

		guiBG = new BufferedImage[1];
		guiBG[0] = ImageIO.read(new File("img/metagame/guiBG.png"));
		
		levelAnim = new LevelAnim[4];
		levelAnim[0] = new LevelAnim("Intro", true);
		levelAnim[1] = new LevelAnim("Wizard", false);
		levelAnim[2] = new LevelAnim("Room", true);
		levelAnim[3] = new LevelAnim("Cave", false);

		charX = 0;
		charY = 0;
		metaChar = ImageIO.read(new File("img/metagame/MetaChar.png"));
		mainChar = new Character(1,1);
		
		wizX = META_MAP_WIDTH-1;
		wizY = META_MAP_HEIGHT-1;
		wizChar = ImageIO.read(new File("img/metagame/MetaWizard.png"));

		enemies = new ArrayList<Enemy>();
		
		
		map = LevelGenerator.makeMetaGame(META_MAP_WIDTH, META_MAP_HEIGHT);
		map[0][0].visible = true;
		map[0][0].type = 0;
		
		map[5][5].visible = true;
		map[5][5].type = 1;
		if(!map[charX][charY].walls[1]) {
			map[charX+1][charY].visible = true;
		}
		if(!map[charX][charY].walls[2]) {
			map[charX][charY+1].visible = true;
		}
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

	public void render(Graphics2D g2) {
		//draw the GUI elements
		g2.setColor(Color.black);
		
		g2.drawImage(guiBG[0], 0, 0, null);
		
		g2.drawImage(levelAnim[ map[charX][charY].type ].next(), LEVEL_ANIM_OFFSET_X, LEVEL_ANIM_OFFSET_Y, null);
		//imgSFX.drawResizedImage(g2, guiBG, 0, 0, GamePanel.PWIDTH, GamePanel.PHEIGHT);

		// Draw the tiles of the map.
		for(int i = 0; i < META_MAP_WIDTH && i < map.length; i++) {
			for(int j = 0; j < META_MAP_HEIGHT && j < map[0].length; j++) {
				if(map[i][j].visible) {
					//Draw the tile image (its type should correspond to the index in tileImages[] that
					//represents it)
					g2.drawImage(tileImages[ map[i][j].type ], i*CELL_SIZE+INGAME_WINDOW_OFFSET_X, 
																j*CELL_SIZE+INGAME_WINDOW_OFFSET_Y, null);
					
					// Draw walls
					if(map[i][j].walls[0]) g2.fillRect(i*CELL_SIZE+INGAME_WINDOW_OFFSET_X, 
														j*CELL_SIZE+INGAME_WINDOW_OFFSET_Y, CELL_SIZE, CELL_SIZE/10);
					if(map[i][j].walls[1]) g2.fillRect((i+1)*CELL_SIZE-CELL_SIZE/10+INGAME_WINDOW_OFFSET_X, 
														j*CELL_SIZE+INGAME_WINDOW_OFFSET_Y, CELL_SIZE/10, CELL_SIZE);
					if(map[i][j].walls[2]) g2.fillRect(i*CELL_SIZE+INGAME_WINDOW_OFFSET_X, 
														(j+1)*CELL_SIZE-CELL_SIZE/10+INGAME_WINDOW_OFFSET_Y, CELL_SIZE, CELL_SIZE/10);
					if(map[i][j].walls[3]) g2.fillRect(i*CELL_SIZE+INGAME_WINDOW_OFFSET_X, 
														j*CELL_SIZE+INGAME_WINDOW_OFFSET_Y, CELL_SIZE/10, CELL_SIZE);
				}
			}
		}

		//Draw the user character.
		g2.drawImage(metaChar,  charX*CELL_SIZE+INGAME_WINDOW_OFFSET_X,  charY*CELL_SIZE+INGAME_WINDOW_OFFSET_Y,  null);
		g2.drawImage(wizChar,  wizX*CELL_SIZE+INGAME_WINDOW_OFFSET_X,  wizY*CELL_SIZE+INGAME_WINDOW_OFFSET_Y,  null);
	}

	public void keyPress(KeyEvent e) {
		//Parse the direction from the given KeyPress
		Point p = InGameState.getDirection(e);

		//Move the main character horizontally
		if(p.x == 1) {
			if(!map[charX][charY].walls[1]) {
				charX += p.x;
			}
		}
		else if(p.x == -1) {
			if(!map[charX][charY].walls[3]) {
				charX += p.x;
			}
		}
		//Move the main character vertically
		if(p.y == -1) {
			if(!map[charX][charY].walls[0]) {
				charY += p.y;
			}
		}
		else if(p.y == 1) {
			if(!map[charX][charY].walls[2]) {
				charY += p.y;
			}
		}

		map[charX][charY].visible = true;
		if(!map[charX][charY].walls[0] && charY > 0) {
			map[charX][charY-1].visible = true;
		}
		if(!map[charX][charY].walls[1] && charX < map.length) {
			map[charX+1][charY].visible = true;
		}
		if(!map[charX][charY].walls[2] && charY < map[0].length) {
			map[charX][charY+1].visible = true;
		}
		if(!map[charX][charY].walls[3] && charX > 0) {
			map[charX-1][charY].visible = true;
		}
	}
}
