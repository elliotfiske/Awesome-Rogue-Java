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
	public static final int INGAME_WINDOW_WIDTH = 54;		// In cells, not pixels
	public static final int INGAME_WINDOW_HEIGHT = 55;		// In cells, not pixels
	public static final int INGAME_SCROLL_PADDING = 10;		// Padding to scroll the viewing window
	public static final int INGAME_SCROLL_MINX = INGAME_SCROLL_PADDING;
	public static final int INGAME_SCROLL_MAXX = INGAME_WINDOW_WIDTH - INGAME_SCROLL_PADDING;
	public static final int INGAME_SCROLL_MINY = INGAME_SCROLL_PADDING;
	public static final int INGAME_SCROLL_MAXY = INGAME_WINDOW_HEIGHT - INGAME_SCROLL_PADDING;

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
		public boolean visible = false, seen = false;
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

		mainChar = new Character(1,1);

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
		for(int i = CAMERA_X; i < CAMERA_X + INGAME_WINDOW_WIDTH && i < map.length; i++) {
			for(int j = CAMERA_Y; j < CAMERA_Y + INGAME_WINDOW_HEIGHT && j < map[0].length; j++) {
				if(map[i][j].visible) {
					g2.drawImage(map[i][j].image, (i-CAMERA_X)*12+INGAME_WINDOW_OFFSET_X,
							(j-CAMERA_Y)*12+INGAME_WINDOW_OFFSET_Y, null);
				}
			}
		}

		mainChar.draw(g2, CAMERA_X, CAMERA_Y);

		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g2);
		}
	}
	// Character calls this if the camera needs to be moved.
	// Also useful for shaking!
	public void moveCamera(int dx, int dy) {
		if(CAMERA_X + dx >= 0 && CAMERA_X + dx + INGAME_WINDOW_WIDTH <= map.length) {
			CAMERA_X += dx;
		}
		if(CAMERA_Y + dy >= 0 && CAMERA_Y + dy + INGAME_WINDOW_HEIGHT <= map[0].length) {
			CAMERA_Y += dy;
		}
	}

	public void keyPress(KeyEvent e) {
		calculateLighting();

		//Parse the direction from the given KeyPress
		Point p = getDirection(e);

		//Move the main character
		mainChar.move(p.x, p.y, map);

		//Move the camera appropriately
		if(mainChar.getX() - CAMERA_X < INGAME_SCROLL_MINX) {
			moveCamera(-1, 0);
		}
		else if(mainChar.getX() - CAMERA_X > INGAME_SCROLL_MAXX) {
			moveCamera(1, 0);
		}

		if(mainChar.getY() - CAMERA_Y < INGAME_SCROLL_MINY) {
			moveCamera(0, -1);
		}
		else if(mainChar.getY() - CAMERA_Y > INGAME_SCROLL_MAXY) {
			moveCamera(0, 1);
		}

		//TODO: Weapons and usable stuff goes here.

		//TODO: Have the enemies take a turn here.

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
		case KeyEvent.VK_NUMPAD7:
			result = new Point(-1, -1);
			break;
		case KeyEvent.VK_U:
		case KeyEvent.VK_NUMPAD8:
			result = new Point(0, -1);
			break;
		case KeyEvent.VK_I:
		case KeyEvent.VK_NUMPAD9:
			result = new Point(1, -1);
			break;
		case KeyEvent.VK_H:
		case KeyEvent.VK_NUMPAD4:
			result = new Point(-1, 0);
			break;
		case KeyEvent.VK_K:
		case KeyEvent.VK_NUMPAD6:
			result = new Point(1, 0);
			break;
		case KeyEvent.VK_N:
		case KeyEvent.VK_NUMPAD1:
			result = new Point(-1, 1);
			break;
		case KeyEvent.VK_M:
		case KeyEvent.VK_NUMPAD2:
			result = new Point(0, 1);
			break;
		case KeyEvent.VK_COMMA:
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
		case KeyEvent.VK_SPACE:
		}

		return result;
	}

	private void initLevel(int levelNum) {
		map = new Tile[60][40];
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				map[i][j] = new Tile(Tile.FLOOR);
				if(i == 0 || i == 5 || j == 0 || j == 5 || i == map.length-1 || j == map[0].length-1) {
					map[i][j].image = tileImages[1];
					map[i][j].blocker = true;
				}
			}
		}

		map[10][4].image = tileImages[0];
		map[10][4].blocker = false;

		map[5][4].image = tileImages[0];
		map[5][4].blocker = false;

		map[15][5].image = tileImages[0];
		map[15][5].blocker = false;

		calculateLighting();
	}

	private void calculateLighting() {
		int x = mainChar.getX(), y = mainChar.getY();
		map[x][y].visible = true;
		for(int tx=0;tx<map.length;tx++) {
			for(int ty=0;ty<map[0].length;ty++) {
				map[tx][ty].visible = false;
			}
		}

		// Gotta do 4 directions
		for(int ix = 1; ix >= -1; ix -= 2) {
			for(int iy = 1; iy >= -1; iy -= 2) {
				// Orthogonal directions first
				for(int dx = 1; x+dx*ix < map.length && x+dx*ix > 0 && Math.abs(dx) <= Character.VISIONRANGE; dx ++) {
					map[x+dx*ix][y].visible = true;
					map[x+dx*ix][y].seen = true;
					if(map[x+dx*ix][y].blocker) break;
				}
				for(int dy = 1; y+dy*iy < map[0].length && y+dy*iy > 0 && Math.abs(dy) <= Character.VISIONRANGE; dy ++) {
					map[x][y+dy*iy].visible = true;
					map[x][y+dy*iy].seen = true;
					if(map[x][y+dy*iy].blocker) break;
				}

				// Now we throw a bunch of rays of different angles
				for(int slope = 1; slope <=63; slope ++) {
					// Initialize v coordinate and set beam size to max
					int v = slope;
					int mini = 0;
					int maxi = 63;

					for(int u=1; mini <= maxi; u ++) {
						int dy = v>>6;
					int dx = u - dy;

					if(dx*dx+dy*dy > Character.VISIONRANGE*Character.VISIONRANGE) break;

					int tx = x+dx*ix;
					int ty = y+dy*iy;
					int cor = 64 - (v&63);

					if(mini < cor && checkCoords(tx, ty)) {
						map[tx][ty].visible = true;
						map[tx][ty].seen = true;
						if(map[tx][ty].blocker) mini = cor;
					}
					if(maxi > cor && checkCoords(tx+ix, ty+iy)) {
						map[tx-ix][ty+iy].visible = true;
						map[tx-ix][ty+iy].seen = true;
						if(map[tx-ix][ty+iy].blocker) maxi = cor;
					}

					v += slope;

					if(!checkCoords(tx, ty)) break;
					
					}
				}
			}
		}
	}

	/**
	 * Ensure that we're in the bounds of the map.
	 * @param x X coordinate of the point to check.
	 * @param y Y coordinate of the point to check.
	 * @return True if we're within the map, false if we're not.
	 */
	private boolean checkCoords(int x, int y) {
		return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
	}
}
