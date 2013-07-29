package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;

public class HulkOff extends Effect {

	public HulkOff() {
		super("Hulk Transition OFF");
	}

	private int currAlpha;
	public static final int MAX_ALPHA = HulkOut.MAX_ALPHA;
	private boolean reversing = false;
	
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		if(!reversing) {
			System.out.println("You shrink back to your normal size.");
			//Get less green an less green an less green till you all outta green
			for(currAlpha = MAX_ALPHA; currAlpha > 0; currAlpha -= 1) {
				Color transGreen = new Color(0, 255, 0, currAlpha);
				g2.setColor(transGreen);
				g2.fillRect(InGameState.INGAME_WINDOW_OFFSET_X, InGameState.INGAME_WINDOW_OFFSET_Y,
						InGameState.INGAME_WINDOW_WIDTH * InGameState.TILE_SIZE, InGameState.INGAME_WINDOW_HEIGHT * InGameState.TILE_SIZE);
			}
		} else {
			//Get greener an greener an greener till you can't green no more
			for(currAlpha = 0; currAlpha < MAX_ALPHA; currAlpha += 1) {
				Color transGreen = new Color(0, 255, 0, currAlpha);
				g2.setColor(transGreen);
	
				g2.fillRect(InGameState.INGAME_WINDOW_OFFSET_X, InGameState.INGAME_WINDOW_OFFSET_Y,
						InGameState.INGAME_WINDOW_WIDTH * InGameState.TILE_SIZE, InGameState.INGAME_WINDOW_HEIGHT * InGameState.TILE_SIZE);
			}
		}
		
		setRunning(false);
	}

	public void render(Graphics2D g2) {
		//waht do i even do heer fader
		System.out.println("render on hulkoff called..?");
	}

	public void reverse() {
		reversing = true;
		setRunning(true);
	}
	
	

}
