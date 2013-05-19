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
	public static final int INGAME_WINDOW_OFFSET_X = 46;	// In pixels, not cells
	public static final int INGAME_WINDOW_OFFSET_Y = 18;	// In pixels, not cells
	public static final int INGAME_WINDOW_WIDTH = 38;		// In cells, not pixels
	public static final int INGAME_WINDOW_HEIGHT = 35;		// In cells, not pixels
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

	private MainCharacter mainChar;

	private ImageSFX imgSFX;

	private BufferedImage guiBG;

	private LevelGenerator levelGen;
	
	protected BufferedImage mapImg;
	private BufferedImage mapImg_t;

	public static ArrayList<String> waitingOn;
	public static boolean suspended = false; //we could just check if waitingOn.size() == 0, but this is faster
	private BufferedImage[] tileImages;
	private BufferedImage[] layovers;
	
	private boolean introLevel;

	public static ArrayList<Enemy> enemyList;
	private ArrayList<Enemy> enemies;
	private Character[][] entities;
	
	private MetaGameState metaGame;
	
	public InGameState(GamePanel gameCanvas, int levelType, MetaGameState metaGame, MainCharacter character) throws IOException {
		this(gameCanvas, false);
		
		this.metaGame = metaGame;
		mainChar = character;

		tileImages = metaGame.getTileImages();

		if(levelType == 0) introLevel = true;
		initLevel(levelType);
	}

	public InGameState(GamePanel gameCanvas) throws IOException {
		this(gameCanvas, true);
	}
	
	public InGameState(GamePanel gameCanvas, boolean needsInit) throws IOException {
		super(gameCanvas);

		layovers = new BufferedImage[3];
		layovers[0] = ImageIO.read(new File("img/Controls_Move.png"));
		layovers[1] = ImageIO.read(new File("img/Controls_Attack.png"));
		layovers[2] = ImageIO.read(new File("img/Controls_Skills.png"));

		imgSFX = new ImageSFX();

		guiBG = ImageIO.read(new File("img/guiBG.png"));

		waitingOn = new ArrayList<String>();

		levelGen = new LevelGenerator();

		enemies = new ArrayList<Enemy>();
		enemyList = new ArrayList<Enemy>();
		
		mapImg = new BufferedImage(INGAME_WINDOW_WIDTH*12, INGAME_WINDOW_HEIGHT*12, BufferedImage.TYPE_INT_ARGB);
		mapImg_t = new BufferedImage(INGAME_WINDOW_WIDTH*12, INGAME_WINDOW_HEIGHT*12, BufferedImage.TYPE_INT_ARGB);

		if(needsInit) {
			initLevel(3);

			mainChar = new MainCharacter(10,10);
		}
	}
	
	public void clearLevel() {
		metaGame.clearCurrentLevel();
		parentPanel().changeGameState(metaGame);
	}

	public void update() {


	}
	
	public Character getMainChar() {
		return mainChar;
	}

	public void render(Graphics2D g2) {
		imgSFX.drawResizedImage(g2, guiBG, 0, 0, GamePanel.PWIDTH, GamePanel.PHEIGHT);
		g2.drawImage(mapImg, INGAME_WINDOW_OFFSET_X, INGAME_WINDOW_OFFSET_Y, null);
	}
	
	public void draw() {
		Graphics2D g2 = (Graphics2D) mapImg_t.getGraphics();
		//draw the GUI elements

		g2.setColor(Color.black);
		g2.clearRect(0,0,GamePanel.PWIDTH, GamePanel.PHEIGHT);
		g2.setColor(Color.white);

		// Draw the tiles of the map.
		for(int i = CAMERA_X; i < CAMERA_X + INGAME_WINDOW_WIDTH && i < map.length; i++) {
			for(int j = CAMERA_Y; j < CAMERA_Y + INGAME_WINDOW_HEIGHT && j < map[0].length; j++) {
				if(map[i][j].visible) {

					//Draw the tile image (its type should correspond to the index in tileImages[] that
					//represents it)
					g2.drawImage(tileImages[ map[i][j].type*2 ], (i-CAMERA_X)*12,
							(j-CAMERA_Y)*12, null);

					if(map[i][j].illustrated) {
						g2.setColor(map[i][j].color);
						g2.fillRect((i-CAMERA_X)*12, (j-CAMERA_Y)*12, 12, 12);
					}
					
					if(map[i][j].getID() != 0) {
						g2.drawString(Integer.toString(map[i][j].getID() % 10), (i-CAMERA_X)*12,
								(j-CAMERA_Y)*12 + 12);
					}

				} else if(map[i][j].seen) {
					//The tile is in our memory.  Draw it, but darkened.

					//TODO: actually darken the tile.  Dunno how to do it right now.
					g2.drawImage(tileImages[ map[i][j].type*2+1 ], (i-CAMERA_X)*12,
							(j-CAMERA_Y)*12, null);
				}
				
				if(map[i][j].illustrated) {
					g2.setColor(map[i][j].color);
					g2.fillRect((i-CAMERA_X)*12, (j-CAMERA_Y)*12, 12, 12);
				}
				
			}
		}
		
		if(introLevel) {
			switch(getMainChar().getRoom()) {
			case 0:
				g2.drawImage(layovers[0], 200, 30, null);
				break;
			case 1:
				g2.drawImage(layovers[1], 300, 200, null);
				break;
			case 2:
				g2.drawImage(layovers[2], 100, 300, null);
				break;
			}
		}

		//Draw the user character.
		mainChar.draw(g2, CAMERA_X, CAMERA_Y);

		//Draw the enemies.
		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			if(map[e.getX()][e.getY()].visible){ 
				enemies.get(i).draw(g2, CAMERA_X, CAMERA_Y);
			}
		}
		
		Graphics2D g = (Graphics2D) mapImg.getGraphics();
		g.drawImage(mapImg_t,  0,  0,  null);
	}

	/**
	 * Character calls this if the camera needs to be moved.
	 * Also useful for shaking!
	 * @param dx How much to move the camera by (x)
	 * @param dy How much to move the camera by (y)
	 */
	public void moveCamera(int dx, int dy) {
		if(CAMERA_X + dx >= 0 && CAMERA_X + dx + INGAME_WINDOW_WIDTH <= map.length) {
			CAMERA_X += dx;
		}
		if(CAMERA_Y + dy >= 0 && CAMERA_Y + dy + INGAME_WINDOW_HEIGHT <= map[0].length) {
			CAMERA_Y += dy;
		}
	}

	public void keyPress(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			clearLevel();
		}
		
		//Parse the direction from the given KeyPress
		Point p = getDirection(e);

		if(!suspended) {
			if(p.x == 0 && p.y == 0) {
				if(e.getKeyCode() == KeyEvent.VK_Z) {
					mainChar.prepareSkill(0);
				}
				else if(e.getKeyCode() == KeyEvent.VK_X) {
					mainChar.prepareSkill(1);
				}
				else if(e.getKeyCode() == KeyEvent.VK_C) {
					mainChar.prepareSkill(2);
				}
			}
			else {
				//Move the main character
				mainChar.move(p.x, p.y, map, entities);
		
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
				for(int i = 0; i < enemies.size(); i ++) {
					Enemy enemy = enemies.get(i);
					if(enemy.dead()) {
						enemies.remove(enemy);
						entities[enemy.getX()][enemy.getY()] = null;
						i -- ;
					}
					else {
						enemy.pathToHeroAndMove(mainChar.getX(), mainChar.getY(), map);
					}
				}
				
				//Wipe tiles of their illustrations
				for(int x = 0; x < map.length; x++) {
					for(int y = 0; y < map[0].length; y++) {
						map[x][y].illustrated = false;
					}
				}
	
				calculateLighting();
			}
		}
		else {
			String waiting = waitingOn.get(waitingOn.size()-1);
			if(waiting.equals("Z")) {
				mainChar.activateSkill(0, p);
			}
			else if(waiting.equals("X")) {
				mainChar.activateSkill(1, p);
			}
			else if(waiting.equals("C")) {
				mainChar.activateSkill(2, p);
			}
		}
	}

	private void initLevel(int levelNum) {
		if(levelNum == 0) {
			map = LevelGenerator.makeLevel(LevelGenerator.INTRO, 38, 35, 1, enemies);

			mainChar.initPos(17, 5);
		}
		else if(levelNum == 2) {
			map = new Tile[60][40];
			for(int i = 0; i < map.length; i ++) {
				for(int j = 0; j < map[0].length; j ++) {
					map[i][j] = new Tile(Tile.FLOOR);
					if(i == 0 || i == 5 || j == 0 || j == 5 || i == map.length-1 || j == map[0].length-1) {
						map[i][j] = new Tile(Tile.WALL);
					}
				}
			}
	
			map[10][4] = new Tile(Tile.FLOOR);
			map[5][4] = new Tile(Tile.FLOOR);
			map[15][5] = new Tile(Tile.FLOOR);
		}
		else if(levelNum == 3) {
			//Generate a sweet new Caves level.
			map = new Tile[80][70];
			levelGen.makeLevel(map, LevelGenerator.CAVE, 80, 70, 1);
			enemies = enemyList;
		}
		
		calculateLighting();
		
		// Fill entity array!
		entities = new Character[map.length][map[0].length];
		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			entities[e.getX()][e.getY()] = e;
		}
		entities[mainChar.getX()][mainChar.getY()] = mainChar;
	}

	private void calculateLighting() {
		int x = mainChar.getX(), y = mainChar.getY();
		for(int tx=0;tx<map.length;tx++) {
			for(int ty=0;ty<map[0].length;ty++) {
				map[tx][ty].visible = false;
			}
		}
		map[x][y].visible = true;

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
				for(int slope = 1; slope <=127; slope ++) {
					// Initialize v coordinate and set beam size to max
					int v = slope;
					int mini = 0;
					int maxi = 127;

					for(int u=1; mini <= maxi; u ++) {
						int dy = v>>7;
					int dx = u - dy;

					if(dx*dx+dy*dy > Character.VISIONRANGE*Character.VISIONRANGE) break;

					int tx = x+dx*ix;
					int ty = y+dy*iy;
					int cor = 128 - (v&127);

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
		
		draw();
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

	/**
	 * Tells you which direction you should go based on a specified key. 
	 * @param e - KeyEvent with desired key.
	 * @return Point where x is the dx component and y is the dy component.
	 */
	public static Point getDirection(KeyEvent e) {
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
}
