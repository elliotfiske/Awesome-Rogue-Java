package com.nightfall.gameEngine;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

public abstract class GameState {
	@SuppressWarnings("unused")
	private GamePanel parentPanel;
	
	public GameState(GamePanel parentPanel) {
		this.parentPanel = parentPanel;
	}
	
	public abstract void update();
	public abstract void render(Graphics g);
	public abstract void keyPress(KeyEvent e);
}
