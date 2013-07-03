package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;

public class HulkOut extends Effect {

	public int duration;
	
	public HulkOut() {
		//TODO: have the screen fade to green
	}
	
	@Override
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		Color transGreen = new Color(0, 255, 0, 10);
		g2.setColor(transGreen);
		g2.fillRect(0, 0, GameFrame.WIDTH, GameFrame.HEIGHT);
	}

	@Override
	public void render(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	/**
	 * Reduces you back to a mere human.
	 */
	public void unHulk() {
		
	}
	
}
