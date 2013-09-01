package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class GameState {
	private GamePanel parentPanel;
	
	public GameState(GamePanel gameCanvas) {
		this.parentPanel = gameCanvas;
	}
	
	public abstract void update();
	public abstract void render(Graphics2D g2);
	public abstract void keyPress(KeyEvent e);
	public abstract void mouseClick(MouseEvent e);
	public GamePanel parentPanel() { return parentPanel; }
}
