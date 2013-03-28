package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public abstract class GameState {
	@SuppressWarnings("unused")
	private GamePanel parentPanel;
	
	public GameState(GamePanel parentPanel) {
		this.parentPanel = parentPanel;
	}
	
	public abstract void update();
	public abstract void render(Graphics2D g2);
	public abstract void keyPress(KeyEvent e);
}
