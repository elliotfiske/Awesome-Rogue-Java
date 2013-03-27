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

		mainChar = new Character(0,0);
		
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
		}
		calculateLighting();
	}

	private void initLevel(int levelNum) {
		map = new Tile[60][40];
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[0].length; j ++) {
				map[i][j] = new Tile(tileImages[0]);
				if(i*i+j*j == 25) {
					map[i][j].image = tileImages[1];
					map[i][j].blocker = true;
				}
			}
		}
		
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

		// Orthogonal directions first
		for(int dx = 1; x+dx < map.length && !map[x+dx][y].blocker; dx ++) {
			map[x+dx][y].visible = true;
			map[x+dx][y].seen = true;
		}
		for(int dy = 1; y+dy < map[0].length && !map[x][y+dy].blocker; dy ++) {
			map[x][y+dy].visible = true;
			map[x][y+dy].seen = true;
		}
		
		// Now we throw a bunch of rays of different angles
		for(int slope = 1; slope <= 31; slope ++) {
			// Initialize v coordinate and set beam size to max
			int v = slope;
			int mini = 0;
			int maxi = 31;
			
			for(int u=1; mini<= maxi && u < 100; u ++) {
				int ty = v>>5-y;
				int tx = u - (y+ty) - x;
				//if(tx >= map.length+1 || tx < 0 || ty >= map[0].length || ty < -1) continue;
				int cor = 32 - (v&31);
				
				if(mini < cor && tx < map.length && tx >= 0 && ty >= 0 && ty < map[0].length) {
					map[tx][ty].visible = true;
					map[tx][ty].seen = true;
					if(map[tx][ty].blocker) mini = cor;
				}
				if(maxi > cor && tx < map.length+1 && tx >= 1 && ty >= -1 && ty < map[0].length-1) {
					map[tx-1][ty+1].visible = true;
					map[tx-1][ty+1].seen = true;
					if(map[tx-1][ty+1].blocker) mini = cor;
				}
				
				v += slope;
			}
		}
	}
}
