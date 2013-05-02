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

public class InGameState extends GameState {
	public static final int INGAME_WINDOW_OFFSET_X = 66;	// In pixels, not cells
	public static final int INGAME_WINDOW_OFFSET_Y = 26;	// In pixels, not cells
	public static final int INGAME_WINDOW_WIDTH = 50;		// In cells, not pixels
	public static final int INGAME_WINDOW_HEIGHT = 50;		// In cells, not pixels
	
	/**
	 * Describes the cell location of the upper lefthand corner of the area
	 * we render.
	 */
	public static int CAMERA_X = 0;
	public static int CAMERA_Y = 0;
	
	
	public Tile[][] map;
	int mapWidth = 0;
	int mapHeight = 0;
	
	private Character mainChar;
	
	private ImageSFX imgSFX;
	
	private BufferedImage[] tileImages;
	private BufferedImage guiBG;
	
	public static ArrayList<String> waitingOn;
	boolean suspended = false; //we could just check if waitingOn.size() == 0, but this is faster
	
	public static ArrayList<Enemy> enemies;
	
	public class Tile { 	
		//list of tile types
		public static final int FLOOR = 0;
		public static final int WALL = 1;
		
		public boolean blocker = false;
		public boolean visible = true, seen = true;
		public BufferedImage image;
		public Tile(int type) {
			switch(type) {
			case FLOOR:
				image = tileImages[0];
				blocker = false;
				break;
			case WALL:
				image = tileImages[1];
				blocker = true;
				break;
			}
			
			
		}
	
		public void doAction() {
			
		}
	}
	
	public InGameState(GamePanel parentPanel) throws IOException {
		super(parentPanel);
		
		imgSFX = new ImageSFX();
		
		tileImages = new BufferedImage[10];
		tileImages[0] = ImageIO.read(new File("img/blankTile.png"));
		tileImages[1] = ImageIO.read(new File("img/blankTile2.png"));
		
		guiBG = ImageIO.read(new File("img/guiBG.png"));
		
		waitingOn = new ArrayList<String>();

		mainChar = new Character(5,5);
		
		enemies = new ArrayList<Enemy>();
		
		initLevel(1);
	}

	public void update() {
		
		
	}

	public void render(Graphics2D g2) {
		//draw the GUI elements
		imgSFX.drawResizedImage(g2, guiBG, 0, 0, GamePanel.PWIDTH, GamePanel.PHEIGHT);
		
		
		g2.setColor(Color.white);
		
		// Draw the map in the viewing window
		for(int i = CAMERA_X; i < CAMERA_X + INGAME_WINDOW_WIDTH - 1; i++) {
			for(int j = CAMERA_Y; j < CAMERA_Y + INGAME_WINDOW_HEIGHT - 1; j++) {
				
				if(map[i][j].visible) {
					g2.drawImage(map[i][j].image, i*12+INGAME_WINDOW_OFFSET_X,
													j*12+INGAME_WINDOW_OFFSET_Y, null);
				}
			}
		}

		mainChar.draw(g2);
		
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g2);
		}
	}

	public void keyPress(KeyEvent e) {
		calculateLighting();
		
		Point p = getDirection(e);
		
		mainChar.move(p.x, p.y, map);

		calculateLighting();
	}
	
	/**
	 * Tells you which direction you should go based on a specified key.
	 * @param e - KeyEvent with desired key.
	 * @return Point where x is the dx component and y is the dy component.
	 */
	public Point getDirection(KeyEvent e) {
		Point result = new Point(0,0);
		
		switch(e.getKeyCode()) {
		case KeyEvent.VK_Y:
			result = new Point(-1, -1);
			break;
		case KeyEvent.VK_U:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_I:
			result = new Point(1, -1);
			break;
		case KeyEvent.VK_H:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_K:
			result = new Point(1, 0);
			break;
		case KeyEvent.VK_N:
			result = new Point(-1, 1);
			break;
		case KeyEvent.VK_M:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_COMMA:
			result = new Point(1, 1);
			break;


		case KeyEvent.VK_NUMPAD7:
			result = new Point(-1, -1);
			break;
		case KeyEvent.VK_NUMPAD8:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_NUMPAD9:
			result = new Point(1, -1);
			break;
		case KeyEvent.VK_NUMPAD4:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_NUMPAD6:
			result = new Point(1, 0);
			break;
		case KeyEvent.VK_NUMPAD1:
			result = new Point(-1, 1);
			break;
		case KeyEvent.VK_NUMPAD2:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_NUMPAD3:
			result = new Point(1, 1);
			break;
			
		case KeyEvent.VK_UP:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_LEFT:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_DOWN:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_RIGHT:
			result = new Point(1, 0);
			break;
		}
		
		return result;
	}

	private void initLevel(int levelNum) {
		
		switch(levelNum) {
		case 1:
			mapWidth = 60;
			mapHeight = 40;
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		
		}
		
		map = new Tile[mapWidth][mapHeight];
		
		for(int i = 0; i < mapWidth; i ++) {
			for(int j = 0; j < mapHeight; j ++) {
				map[i][j] = new Tile(Tile.FLOOR);
				
				//add walls
				if(i == 0 || i == mapWidth - 1 || j == 0 || j == mapHeight - 1) {
					map[i][j] = new Tile(Tile.WALL);
				}
			}
		}
		
		calculateLighting();
	}
	
	private void calculateLighting() {
		
	}
}

//TODO in the next 30 minutes implement the interface you drew.
