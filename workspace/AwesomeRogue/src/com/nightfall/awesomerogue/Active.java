package com.nightfall.awesomerogue;

public class Active {
	public static final int EMPTY_SLOT = 0;
	public static final int GRENADE_LAUNCHER = 1;
	public static final int FALCON_PUNCH = 2;
	
	/** number of implemented actives.  Be sure to update this if you add an active! */
	public static final int NUM_ACTIVES = 2;
	
	public void doActive(int whichActive) {
		switch(whichActive) {
		case FALCON_PUNCH: 
			falconPunch();
			break;
		case GRENADE_LAUNCHER:
			grenadeLauncher();
			break;
		}
	}
	
	private void falconPunch() {
		
	}
	
	private void grenadeLauncher() {
		
	}
}
