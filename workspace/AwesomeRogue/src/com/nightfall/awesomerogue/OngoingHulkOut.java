package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;

public class OngoingHulkOut extends OngoingEffect {
	
	public int duration;
	private MainCharacter mainChar;
	
	public OngoingHulkOut(int duration, MainCharacter mainChar) {
		this.duration = duration;
		this.mainChar = mainChar;
		
		setIntro(new HulkOut());
		setOutro(new HulkOff());
		
		mainChar.setHulking(true);
	}

	@Override
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		
		Color transGreen = new Color(0, 255, 0, HulkOut.MAX_ALPHA);
		g2.setColor(transGreen);
		g2.fillRect(0, 0, 500, 500);
		
	}

	@Override
	public void render(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnIterate(Tile[][] map, Character[][] entities) {
		duration--;
		
		if(duration <= 0) {
			setRunning(false);
			mainChar.setHulking(false);
		}
		
	}
	
	
}
