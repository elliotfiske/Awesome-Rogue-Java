package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;

public class OngoingHulkOut extends OngoingEffect {
	
	public int duration;
	private MainCharacter mainChar;
	
	public OngoingHulkOut(int duration, MainCharacter mainChar) {
		super("Ongoing hulk out");
		
		this.duration = duration;
		this.mainChar = mainChar;
		
		setIntro(new HulkOut());
		setOutro(new HulkOff());
		
		mainChar.setHulking(true);
	}

	public void renderAndIterate(Graphics2D g2) {
		Color transGreen = new Color(0, 255, 0, HulkOut.MAX_ALPHA);
		g2.setColor(transGreen);
		g2.fillRect(InGameState.INGAME_WINDOW_OFFSET_X, InGameState.INGAME_WINDOW_OFFSET_Y,
				InGameState.INGAME_WINDOW_WIDTH * InGameState.TILE_SIZE, InGameState.INGAME_WINDOW_HEIGHT * InGameState.TILE_SIZE);
	}

	@Override
	public void render(Graphics2D g2) {
		Color transGreen = new Color(0, 255, 0, HulkOut.MAX_ALPHA);
		g2.setColor(transGreen);
		g2.fillRect(InGameState.INGAME_WINDOW_OFFSET_X, InGameState.INGAME_WINDOW_OFFSET_Y,
				InGameState.INGAME_WINDOW_WIDTH * InGameState.TILE_SIZE, InGameState.INGAME_WINDOW_HEIGHT * InGameState.TILE_SIZE);
	}

	@Override
	public void turnIterate(Tile[][] map, Character[][] entities) {
		duration--;
		
		if(duration <= 0) {
			setRunning(false);
			mainChar.setHulking(false);
		}
	}
	
	public void reverse() {
		//Nothing changes for this guy at least.
		setRunning(true);
	}

	@Override
	public void iterate(long deltaTime) {
		duration--;
		
		if(duration <= 0) {
			setRunning(false);
			mainChar.setHulking(false);
		}
	}
	
}
