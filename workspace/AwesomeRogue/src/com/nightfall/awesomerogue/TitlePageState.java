package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TitlePageState extends GameState {	
	public TitlePageState(GamePanel parentPanel) {
		super(parentPanel);
	}

	public void update() {
	}

	public void render(Graphics2D g2) {
		g2.setColor(Color.white);
		g2.drawString("Welcome to Awesome Rogue.", 100, 100);
	}

	public void keyPress(KeyEvent e) {
		
	}
	
	public void mouseClick(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		System.out.println("Mouseclick at " + x + ", " + y);
	}


}
