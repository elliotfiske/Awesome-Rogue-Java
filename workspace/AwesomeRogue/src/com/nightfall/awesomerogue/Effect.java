package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

public abstract class Effect {
	private boolean running;
	
	public Effect() {
		running = true;
	}
	
	public boolean running() { return running; }
	public void setRunning(boolean running) { this.running = running; }
	
	// Extra params cause YOU NEVER KNOW WHAT WE GONNA COOK UP THIS TIME
	public abstract void renderAndIterate(Graphics2D g2, Tile[][] map, Character[][] entities);
	public abstract void render(Graphics2D g2);
}
