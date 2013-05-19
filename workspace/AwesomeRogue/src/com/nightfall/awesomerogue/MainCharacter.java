package com.nightfall.awesomerogue;


public class MainCharacter extends Character {
	public static final int VISIONRANGE = 35;
	
	private int awesome;
	
	public MainCharacter(int x, int y) {
		super(x, y, "@");
		awesome = 100;
	}
	public int getAwesome() { return awesome; }
}
