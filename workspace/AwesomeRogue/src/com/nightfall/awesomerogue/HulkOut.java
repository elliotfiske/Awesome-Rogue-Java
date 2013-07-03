package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;

public class HulkOut extends Effect {
	
	private int currAlpha;
	public static final int MAX_ALPHA = 150;
	
	@Override
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		//Get greener an greener an greener till you can't green no more
		for(currAlpha = 0; currAlpha < MAX_ALPHA; currAlpha += 1) {
			Color transGreen = new Color(0, 255, 0, currAlpha);
			g2.setColor(transGreen);
			g2.fillRect(0, 0, 500, 500);
		}
		
		setRunning(false);
	}

	@Override
	public void render(Graphics2D g2) {
		// TODO Auto-generated method stub
		//Not sure this matters..?
	}
	
}