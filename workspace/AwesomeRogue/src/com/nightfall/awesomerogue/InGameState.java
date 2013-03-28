package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InGameState extends GameState {
	public static final int INGAME_WINDOW_OFFSET_X = 25;	// In pixels, not cells
	public static final int INGAME_WINDOW_OFFSET_Y = 25;	// In pixels, not cells
	public static final int INGAME_WINDOW_WIDTH = 60;		// In cells, not pixels
	public static final int INGAME_WINDOW_HEIGHT = 40;		// In cells, not pixels
	
	private Tile[][] map;
	
	private Character mainChar;
	
	private BufferedImage[] tileImages;
	
	private class Tile {
		public boolean blocker = false;
		public boolean visible = false, seen = false;
		public BufferedImage image;
		public Tile(BufferedImage img) {
			image = img;
		}
	
		public void doAction() {
			
		}
	}
	
	public InGameState(GamePanel parentPanel) throws IOException {
		super(parentPanel);
		
		tileImages = new BufferedImage[10];
		tileImages[0] = ImageIO.read(new File("img/blankTile.png"));
		tileImages[1] = ImageIO.read(new File("img/blankTile2.png"));

		mainChar = new Character(1,1);
		
		initLevel(1);
	}

	public void update() {
		int x = (int) (Math.random()*60);
		int y = (int) (Math.random()*40);
		if(map[x][y].image == tileImages[0]) {
			//map[x][y].image = tileImages[1];
		}
		else {
			//map[x][y].image = tileImages[0];
		}
		
		float totalZero = 0;
		for(int i = 0; i < INGAME_WINDOW_WIDTH; i ++) {
			for(int j = 0; j < INGAME_WINDOW_HEIGHT; j ++) {
				if(map[i][j].image == tileImages[0]) {
					totalZero ++;
				}
			}
		}
		
		//System.out.println(( totalZero / 24) + "% is green");
	}

	public void render(Graphics2D g2) {
		g2.setColor(Color.white);
		
		// Draw the map in the viewing window
		for(int i = 0; i < INGAME_WINDOW_WIDTH; i ++) {
			for(int j = 0; j < INGAME_WINDOW_HEIGHT; j ++) {
				if(map[i][j].visible) {
					g2.drawImage(map[i][j].image, i*12+INGAME_WINDOW_OFFSET_X,
													j*12+INGAME_WINDOW_OFFSET_Y, null);
				}
			}
		}

		mainChar.draw(g2);
	}

	public void keyPress(KeyEvent e) {
		calculateLighting();
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			mainChar.move(0, -1);
			break;
		case KeyEvent.VK_DOWN:
			mainChar.move(0, 1);
			break;
		case KeyEvent.VK_LEFT:
			mainChar.move(-1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			mainChar.move(1, 0);
			break;
		case KeyEvent.VK_SPACE:
		}
		calculateLighting();
	}

	private void initLevel(int levelNum) {
		map = new Tile[60][40];
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				map[i][j] = new Tile(tileImages[0]);
				if(i == 0 || i == 5 || j == 0 || j == 5 || i == map.length-1 || j == map[0].length-1) {
					map[i][j].image = tileImages[1];
					map[i][j].blocker = true;
				}
			}
		}

		map[10][4].image = tileImages[0];
		map[10][4].blocker = false;
		
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
			for(int slope = 1; slope <=31; slope ++) {
				// Initialize v coordinate and set beam size to max
				int v = slope;
				int mini = 0;
				int maxi = 31;
				
				for(int u=1; mini <= maxi; u ++) {
					int dy = v>>5;
					int dx = u - dy;
					
					if(dx*dx+dy*dy > Character.VISIONRANGE*Character.VISIONRANGE) break;

					int tx = x+dx*ix;
					int ty = y+dy*iy;
					int cor = 32 - (v&31);
					
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
	
	private boolean checkCoords(int x, int y) {
		return x >= 0 && x < map.length && y >= 0 && y < map[0].length;
	}
}
