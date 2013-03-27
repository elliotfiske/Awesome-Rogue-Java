package com.nightfall.gameEngine;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DefaultGameState extends GameState {
	private BufferedImage background;
	private BufferedImage sprite;
	
	private int spriteX, spriteY;
	
	public DefaultGameState(GamePanel parentPanel) {
		super(parentPanel);
		try {
			background = ImageIO.read(new File("img/background.png"));
			sprite = ImageIO.read(new File("img/icon.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		spriteX = spriteY = 50;
	}
	
	public void update() {
		
	}

	public void render(Graphics g) {
		g.drawImage(background, 0, 0, null);
		g.drawImage(sprite, spriteX, spriteY, null);
	}
	
	public void keyPress(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			spriteY -= 10;
			break;
		case KeyEvent.VK_DOWN:
			spriteY += 10;
			break;
		case KeyEvent.VK_LEFT:
			spriteX -= 10;
			break;
		case KeyEvent.VK_RIGHT:
			spriteX += 10;
			break;
		}
	}
}
