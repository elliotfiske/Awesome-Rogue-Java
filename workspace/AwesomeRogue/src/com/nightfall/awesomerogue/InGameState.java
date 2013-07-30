package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
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

	public static final int TILE_SIZE = 12;

	//Enable to debug stuff
	public static final boolean GODMODE_VISION = true;
	public static final boolean GODMODE_DRAW_IDS = false;
	public static final boolean GODMODE_WALKTHRUWALLS = false;
	public static final boolean GODMODE_CAN_FREEZE_ENEMIES = true;
	private boolean areEnemiesFrozen = false;
	public static final boolean EVERY_PASSIVE_UNLOCKED = true;
	public static final boolean GODMODE_EARTHQUAKE = true;

	/**
	 * Describes the cell location of the upper lefthand corner of the area
	 * we render.
	 */
	public static int CAMERA_X = 0;
	public static int CAMERA_Y = 0;


	public static Tile[][] map;
	int mapWidth = 0;
	int mapHeight = 0;

	private MainCharacter mainChar;
	
	private ImageSFX imgSFX;

	private BufferedImage guiBG;

	private LevelGenerator levelGen;

	protected BufferedImage mapImg;
	private BufferedImage mapImg_t;

	public static ArrayList<String> waitingOn;
	private static ArrayList<Effect> effects;
	private static ArrayList<OngoingEffect> ongoingEffects;
	public static boolean suspended = false; //we could just check if waitingOn.size() == 0, but this is faster
	public boolean takeEnemyTurn = false;

	private BufferedImage[] tileImages;
	private BufferedImage[] layovers;

	private boolean introLevel;

	public static ArrayList<Enemy> enemies;
	public static ArrayList<Character> pets;
	private static Character[][] entities;
	
	public int prevHealth;
	private static ArrayList<FloatyText> texts;
	/** Lets us see how wide a string actually is rendered */
	Font defaultFont;
	FontMetrics fontMetrics;

	private static ArrayList<Turn> pastTurns;
	private static Turn currentTurn;
	public static boolean REWINDING = false;
	
	private MetaGameState metaGame;

	public InGameState(GamePanel gameCanvas, int levelType, MetaGameState metaGame, MainCharacter character) throws IOException {
		this(gameCanvas, false);

		this.metaGame = metaGame;
		mainChar = character;
		mainChar.setLevel(this);
		prevHealth = mainChar.getHealth();

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

		effects = new ArrayList<Effect>();
		ongoingEffects = new ArrayList<OngoingEffect>();

		guiBG = ImageIO.read(new File("img/guiBG.png"));

		waitingOn = new ArrayList<String>();
		suspended = false;

		levelGen = new LevelGenerator();

		enemies = new ArrayList<Enemy>();
		
		pets = new ArrayList<Character>();
		
		texts = new ArrayList<FloatyText>();
		pastTurns = new ArrayList<Turn>();
		currentTurn = new Turn();
		
		defaultFont = new Font("Helvetica", Font.PLAIN, 12);
		//fontMetrics = new Graphics.getFontMetrics(defaultFont);         

		mapImg = new BufferedImage(INGAME_WINDOW_WIDTH*TILE_SIZE, INGAME_WINDOW_HEIGHT*TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
		mapImg_t = new BufferedImage(INGAME_WINDOW_WIDTH*TILE_SIZE, INGAME_WINDOW_HEIGHT*TILE_SIZE, BufferedImage.TYPE_INT_ARGB);

		if(needsInit) {
			initLevel(3);

			System.out.println("eh?");
			mainChar = new MainCharacter(10,10,map);
			mainChar.setLevel(this);
		}
	}

	public void clearLevel() {
		metaGame.clearCurrentLevel();
		parentPanel().changeGameState(metaGame);
	}

	public void update() {
		
		if(suspended) {
			String waiting = waitingOn.get(waitingOn.size()-1);
			mainChar.update(map, entities);
			
			//update amigas
			for(Character pet : pets) {
				pet.update(map, entities);
			}
			
			//update ENamigas
			for(Character e : enemies) {
				e.update(map, entities);
			}
			calculateLighting();
		} else {
			//Have the enemies take a turn if they deserve it.
			if(takeEnemyTurn) {
				enemyTurn();
				takeEnemyTurn = false;
			}
		}
	}

	public static void newOngoingEffect(OngoingEffect oe) {
		//Run the intro of the ongoing effect
		waitOn(oe.getIntro());
		ongoingEffects.add(oe);
	}
	
	public static void waitOn(Effect effect) {
		waitOn("effect" + effect.getName());
		effects.add(effect);
		addEvent(new Event.EffectHappened(effect));
	}

	public static void waitOn(String event) {
//		System.out.println("Added " + event);
		waitingOn.add(event);
		suspended = true;
	}

	public static void endAllWaits(String event) {	// Same as endWait but removes all instances of the wait
		while(waitingOn.remove(event));
		if(InGameState.waitingOn.size() == 0)
			InGameState.suspended = false;	// Gotta unpause!
	}

	public static void endWait(String event) {
//		System.out.println("removing " + event);
		waitingOn.remove(event);
		if(InGameState.waitingOn.size() == 0)
			InGameState.suspended = false;	// Gotta unpause!
	}

	public Character getMainChar() {
		return mainChar;
	}

	public void render(Graphics2D g2) {
		//Update the floatytexts
		for(int i = 0; i < texts.size(); i++) {
			FloatyText ft = texts.get(i);
			ft.update();
			
			draw();
		}
		
		imgSFX.drawResizedImage(g2, guiBG, 0, 0, GamePanel.PWIDTH, GamePanel.PHEIGHT);
		g2.drawImage(mapImg, INGAME_WINDOW_OFFSET_X, INGAME_WINDOW_OFFSET_Y, null);

		boolean effectHappening = false;
		boolean effectOngoing = false;
		
		if(effects.size() > 0) {
			effectHappening = true;
		}
		
		if(ongoingEffects.size() > 0) {
			effectOngoing = true;
		}
		
		if(effectHappening) {
			for(int i = 0; i < effects.size(); i++) {
				Effect e = effects.get(i);
				e.renderAndIterate(g2, map, entities);
				if(!e.running()) {
					endWait("effect" + e.getName());
					effects.remove(i--); // and decrement i so we don't skip an effect
				}
			}
		}
		
		if(effectOngoing) {
			for(int i = 0; i < ongoingEffects.size(); i++) {
				OngoingEffect e = ongoingEffects.get(i);
				e.renderAndIterate(g2, map, entities);
				if(!e.running()) {
					//Run the outro of the ongoing effect and take it out
					waitOn(e.getOutro());
					ongoingEffects.remove(i--);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	public void draw() {
		Graphics2D g2 = (Graphics2D) mapImg_t.getGraphics();
		//draw the GUI elements

		g2.setColor(Color.black);
		g2.clearRect(0,0,GamePanel.PWIDTH, GamePanel.PHEIGHT);
		g2.setColor(Color.white);

		// Draw the tiles of the map.
		for(int i = CAMERA_X; i < CAMERA_X + INGAME_WINDOW_WIDTH && i < map.length; i++) {
			for(int j = CAMERA_Y; j < CAMERA_Y + INGAME_WINDOW_HEIGHT && j < map[0].length; j++) {
				// Don't draw the void!!
				if(map[i][j].type == Tile.VOID) continue; 
				if(map[i][j].visible || GODMODE_VISION) { //TODO: Switch back from god-mode vision

					//Draw the tile image (its type should correspond to the index in tileImages[] that
					//represents it)
					g2.drawImage(tileImages[ map[i][j].type*2 ], (i-CAMERA_X)*TILE_SIZE,
							(j-CAMERA_Y)*TILE_SIZE, null);

					if(map[i][j].illustrated) {
						g2.setColor(map[i][j].color);
						g2.fillRect((i-CAMERA_X)*TILE_SIZE, (j-CAMERA_Y)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
					}

					if(map[i][j].getID() != 0 && GODMODE_DRAW_IDS) {
						g2.drawString(Integer.toString(map[i][j].getID() % 10), (i-CAMERA_X)*TILE_SIZE,
								(j-CAMERA_Y)*TILE_SIZE + TILE_SIZE);
					}

				} else if(map[i][j].seen) {
					//The tile is in our memory.  Draw it, but darkened.

					//TODO: actually darken the tile.  Dunno how to do it right now
					g2.drawImage(tileImages[ map[i][j].type*2+1 ], (i-CAMERA_X)*TILE_SIZE,
							(j-CAMERA_Y)*TILE_SIZE, null);
				}

				if(map[i][j].illustrated) {
					g2.setColor(map[i][j].color);
					g2.fillRect((i-CAMERA_X)*TILE_SIZE, (j-CAMERA_Y)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
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
			Character e = enemies.get(i);
			if(e.dead()) {
				enemies.remove(i--);
				continue;
			}
			if(map[e.getX()][e.getY()].visible || GODMODE_VISION){ 
				e.draw(g2, CAMERA_X, CAMERA_Y);
			} else if(mainChar.hasPassive(Passive.XRAY_GOGGLES)) {
				g2.setColor(Color.red);
				g2.drawString("?", (e.getX()-CAMERA_X)*TILE_SIZE, (e.getY()-CAMERA_Y)*TILE_SIZE + TILE_SIZE );
			}
		}
		
		//Draw the pets
		for(int i = 0; i < pets.size(); i++) {
			Character p = pets.get(i);
			if(p.dead()) {
				//BURY IT
				pets.remove(i--);
				continue; //MOVE ON.
			}
			
			//Gonna make it so that you can see through pet's eyes maybe?
			p.draw(g2, CAMERA_X, CAMERA_Y);
		}

		//draw floaty texts :3
		for(int f = 0; f < texts.size(); f++) {
			texts.get(f).draw(g2);
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
		if(CAMERA_X + dx < 0) {
			CAMERA_X = 0;
		} else if(CAMERA_X + dx + INGAME_WINDOW_WIDTH > map.length) {                                                             
			CAMERA_X = map.length - INGAME_WINDOW_WIDTH;
		} else {
			CAMERA_X += dx;
		}
		
		if(CAMERA_Y + dy < 0) {
			CAMERA_Y = 0;
		} else if(CAMERA_Y + dy + INGAME_WINDOW_HEIGHT > map[0].length) {                                                             
			CAMERA_Y = map[0].length - INGAME_WINDOW_HEIGHT;
		} else {
			CAMERA_Y += dy;
		}
	}

	public void keyPress(KeyEvent e) {
//		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
//			LevelInfo.MAX_FEATURES ++;
//			initLevel(LevelInfo.ROOMS);
//		}

		//DEBUG: are we suspended?
		if(e.getKeyCode() == KeyEvent.VK_S) {
			System.out.print("Are we suspended? ");
			if(suspended) {
				System.out.println("WE SURE ARE! Dumping waitstack:");
				for(String s : waitingOn) {
					System.out.println(s);
				}
			} else {
				System.out.println("WE ARE NOT NOT NOT!");
			}
		}
		
		//Smartmove if we're waiting on smartmove and it's at the top of the stack
		if(waitingOn.size() > 0 && waitingOn.get(0) == "smartmove") {
			endWait("smartmove");
		}
		
		//Parse the direction from the given KeyPress
		Point p = getDirection(e);

		//Wipe tiles of their illustrations
		for(int x = 0; x < map.length; x++) {
			for(int y = 0; y < map[0].length; y++) {
				map[x][y].illustrated = false;
			}
		}

		if(e.getKeyCode() == KeyEvent.VK_F && GODMODE_CAN_FREEZE_ENEMIES) {
			areEnemiesFrozen = !areEnemiesFrozen;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_T && GODMODE_EARTHQUAKE) {
			for(int i = 0; i < enemies.size(); i++) {
				//Choose random direction
				int randDirection = (int) (Math.random() * 8);
				//convert to point coords
				Point randDirectionP = Enemy.getPointDirection(randDirection);
				//Shove 'em!
				enemies.get(i).forceMarch(randDirectionP.x * 5, randDirectionP.y * 5);
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_E) {
			for(int i = 0; i < enemies.size(); i++) {
				Character en = enemies.get(i);
				((Enemy) en).getHealed(100);
			}
		}

		if(!suspended) {
			
			//Smartmove
			if(e.getKeyCode() == KeyEvent.VK_NUMPAD5) {
				endAllWaits("smartmove");
				waitOn("smartmove");
			}
			
			if(e.getKeyCode() == KeyEvent.VK_R) {
				undoLastTurn();
			}
			
			if(p.x == 0 && p.y == 0) {
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					InGameState.waitOn("attack");
				}
				if(e.getKeyCode() == KeyEvent.VK_Z) {
					mainChar.prepareSkill(0);
				}
				else if(e.getKeyCode() == KeyEvent.VK_X) {
					mainChar.prepareSkill(1);
				}
				else if(e.getKeyCode() == KeyEvent.VK_C) {
					mainChar.prepareSkill(2);
				}
			} else {
				//Move the main character
				mainChar.move(p.x, p.y, map, entities);
				
				updateCamera();

				//TODO: Weapons and usable stuff goes here.


				//Enemy time!
				queueEnemyTurn();
				
				//Iterate the iteratable effects here (the dead ones are removed in render() )
				for(int i = 0; i < ongoingEffects.size(); i++) {
					OngoingEffect oe = ongoingEffects.get(i);
					oe.turnIterate(map, entities);
				}
				
				//Move pets!
				for(int i = 0; i < pets.size(); i++) {
					Character pet = pets.get(i);
					pet.takeTurn(mainChar, map);
				}

				calculateLighting();
			}
		}
		else {
			String waiting = waitingOn.get(waitingOn.size()-1);
			if(waiting.equals("attack")) {
				mainChar.attack(p);
				endWait("attack");
				queueEnemyTurn();
			}
			if(waiting.equals("Z")) {
				mainChar.activateSkill(0, p);
				queueEnemyTurn();
				mainChar.addAwesome(10);
			}
			else if(waiting.equals("X")) {
				mainChar.activateSkill(1, p);
				queueEnemyTurn();
			}
			else if(waiting.equals("C")) {
				mainChar.activateSkill(2, p);
				queueEnemyTurn();
			}
		}
	}

	/**
	 *  This makes it so that the order of operations is shoot -> bullet finishes -> enemies attack
	 *  
	 *  This way, enemies don't get to take a turn right as you shoot and dodge your bullets, which is
	 *  annoying.
	 */
	private void queueEnemyTurn() {
		takeEnemyTurn = true;
	}
	
	/** All the enemies take a turn */
	private void enemyTurn() {
		
		for(int i = 0; i < enemies.size(); i ++) {
			Character enemy = enemies.get(i);
			if(enemy.dead()) {
				enemies.remove(enemy);
				entities[enemy.getX()][enemy.getY()] = null;
				i--;
			} else {
				if(!areEnemiesFrozen) {
					((Enemy) enemy).takeTurn(mainChar, map);
				}
			}
		}
		
		beginNewTurn();
		
	}

	/** Called at the start of every turn. */
	public void beginNewTurn() {
		calculateLighting();
		
		//Calculate how much health the player lost this turn, display with FloatyText
		int lostHealth = prevHealth - mainChar.getHealth();
		hitText(mainChar.x, mainChar.y, lostHealth);
		prevHealth = mainChar.getHealth();
		
		//Add the latest event to the event stack and wipe currentEvent
		pastTurns.add(currentTurn);
		currentTurn = new Turn();
		
		//Wipe floatytexts that are past their prime
		for(int i = texts.size() - 1; i >= 0; i--) {
			if(texts.get(i).alpha <= 0) {
				texts.remove(i);
			}
		}
	}
	
	/**
	 * Update the camera.  Used for teleporting or force marching
	 */
	public void updateCamera() {
		if(mainChar.getX() - CAMERA_X < INGAME_SCROLL_MINX) {
			int cameraMoveDistance = INGAME_SCROLL_MINX - (mainChar.getX() - CAMERA_X);
			moveCamera(-cameraMoveDistance, 0);
		}
		else if(mainChar.getX() - CAMERA_X > INGAME_SCROLL_MAXX) {
			int cameraMoveDistance = mainChar.getX() - CAMERA_X - INGAME_SCROLL_MAXX;
			moveCamera(cameraMoveDistance, 0);
		}

		if(mainChar.getY() - CAMERA_Y < INGAME_SCROLL_MINY) {
			int cameraMoveDistance = INGAME_SCROLL_MINY - (mainChar.getY() - CAMERA_Y);
			moveCamera(0, -cameraMoveDistance);
		}
		else if(mainChar.getY() - CAMERA_Y > INGAME_SCROLL_MAXY) {
			int cameraMoveDistance = mainChar.getY() - CAMERA_Y - INGAME_SCROLL_MAXY;
			moveCamera(0, cameraMoveDistance);
		}
	}

	private void initLevel(int levelNum) {
		//LevelInfo thisInfo = new LevelInfo(levelNum, 1);
		LevelInfo thisInfo = new LevelInfo(levelNum, 2);
		map = thisInfo.getMap();
		enemies = thisInfo.getEnemies();
		mainChar.initPos(thisInfo.getStartPos());
		
		/*
		if(levelNum == 0) {
			map = LevelGenerator.makeLevel(LevelInfo.INTRO, 38, 35, 1, enemies);

			mainChar.initPos(17, 5);
		}
		else if(levelNum == 2) {
			map = LevelGenerator.makeLevel(LevelGenerator.ROOMS, 80, 60, 1, enemies);

			mainChar.initPos(40, 30);
		}
		else if(levelNum == 3) {
			//Generate a sweet new Caves level.
			map = new Tile[80][70];
			levelGen.makeLevel(map, LevelGenerator.CAVE, 80, 70, 2);

			mainChar.initPos(10, 10);
			
			//give the main character a map he's lost
			mainChar.giveMap(map);

			enemies = enemyList;
		}
*/
		calculateLighting();

		// Fill entity array!
		entities = new Character[map.length][map[0].length];
		for(int i = 0; i < enemies.size(); i++) {
			Character e = enemies.get(i);
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

	public static void addPet(Pet pet) {
		pets.add(pet);
	}

	/**
	 * Helper method to grab a specified Tile from the map, and handle outta bounds problems.
	 * @param x X coord of the tile to grab.
	 * @param y Y coord of the tile to grab.
	 */
	public static Tile tileAt(int x, int y) {
		if(x < 0) {
			System.out.println("X is negative! (" + x + ") Adjusting to 0.");
			x = 0;
		}

		if(x >= map.length) {
			System.out.println("X is too big! (" + x + ") Adjusting to " + map.length);
			x = map.length;
		}

		if(y < 0) {
			System.out.println("Y is negative! (" + y + ") Adjusting to 0.");
			y = 0;
		}

		if(y >= map[0].length) {
			System.out.println("Y is too big! (" + y + ") Adjusting to " + map[0].length);
			y = map.length;
		}

		return map[x][y];
	}

	/**
	 * Handle to the entities array
	 * @return The array containing every enemy/character/pet
	 */
	public static Character[][] getEntities() {
		return entities;
	}
	
	/**
	 * Enemies weren't dying properly.  How rude.
	 * @param e The enemy to remove
	 */
	public static void removeEnemy(Enemy e) {
		enemies.remove(e);
	}

	/** Creates some floating text showing you how much awesome you just got */
	public void awesomeText(int x, int y, int amount) {
		FloatyText text = new FloatyText(x, y, "+" + amount + " Awesome!", Color.blue);
		texts.add(text);
	}
	
	/** Floating text saying how much you got hurt. */
	public void hitText(int x, int y, int amount) {
		if(amount > 0) {
			FloatyText text = new FloatyText(x, y, "-" + amount + " Health!", Color.red);
			texts.add(text);
		}
	}
	
	/** Floaty text saying how much was healed.  If it's an enemy make the message
	 * a nasty shade of vomit-green, otherwise a nice healthy green. */
	public static void healText(int x, int y, int amount, boolean isEnemy) {
		Color healGreen = Color.green;
		
		if(isEnemy) {
			healGreen = new Color(136, 164, 0); //ew
		}
		
		if(amount > 0) {
			FloatyText text = new FloatyText(x, y, "+" + amount + " Health!", healGreen);
			texts.add(text);
		}
	}
	
	private static class FloatyText {
		float x, y;
		String message;
		Color color;
		int alpha;
		
		/**
		 * Make a new floaty text
		 * @param x TILE X
		 * @param y TILE Y
		 * @param message Text to display
		 * @param color Color to display the text to display
		 */
		public FloatyText(int x, int y, String message, Color color) {
			this.x = x * TILE_SIZE;
			this.y = y * TILE_SIZE;
			this.message = message;
			this.color = color;
			alpha = 255;
		}
		
		public void draw(Graphics2D g2) {
			Color color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), alpha);
			g2.setColor(color);
			g2.drawString(message, x - CAMERA_X * TILE_SIZE, y - CAMERA_Y * TILE_SIZE);
		}
		
		public void update() {
			alpha-=4;
			if(alpha < 0) alpha = 0;
			y -= 0.05;
		}
	}

	/** Wall -> floor at destroyX, destroyY */
	public static void demolish(int destroyX, int destroyY) {
		map[destroyX][destroyY] = new Tile(Tile.FLOOR, destroyX, destroyY);
		addEvent(new Event.MapChange(new Tile(Tile.FLOOR, destroyX, destroyY), new Tile(Tile.WALL, destroyX, destroyY)));
	}
	
	public static void addEvent(Event event) {
		currentTurn.addEvent(event);
	}
	
	public void undoLastTurn() {
		//If the level just started, tell the user that.
		//(alternatively, add a secret feature that throws you back to the metagame?
		//that way you could check out a level to see if it's to your liking then rewind
		//and choose another one.
		if(pastTurns.isEmpty()) {
			System.out.println("Nothing to rewind!");
			return;
		}
		
		//Bring up the last event that happened
		Turn turnToUndo = pastTurns.get(pastTurns.size() - 1);
		pastTurns.remove(turnToUndo);
		
		//Go through the turn and undo each event that happened
		//"REWINDING" makes sure that events that happen while rewinding aren't recorded
		REWINDING = true;
		while(!turnToUndo.isEmpty()) {
			Event lastEvent = turnToUndo.getLastEvent();
			lastEvent.undo();
		}
		REWINDING = false;
		
		calculateLighting();
		updateCamera();
	}
}
