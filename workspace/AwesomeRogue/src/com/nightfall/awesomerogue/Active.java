package com.nightfall.awesomerogue;

import java.awt.Point;

public class Active {
	public static final int EMPTY_SLOT = 0;
	public static final int GRENADE_LAUNCHER = 1;
	public static final int FALCON_PUNCH = 2;
	public static final int DRILL_DOZER = 3;
	public static final int HULK_SERUM = 4;
	public static final int ICE_CROWN = 5;
	
	/** number of implemented actives.  Be sure to update this if you add an active! */
	public static final int NUM_ACTIVES = 5;
	
	MainCharacter mainChar;
	
	public Active(MainCharacter mainChar) {
		//Get a handle to the main character
		this.mainChar = mainChar;
	}
	
	public void prepareActive(int whichActive) {
		switch(whichActive) {
		case FALCON_PUNCH: 
			prepFalconPunch();
			break;
		case GRENADE_LAUNCHER:
			prepGrenadeLauncher();
			break;
		case DRILL_DOZER:
			prepDrillDozer();
			break;
		case HULK_SERUM:
			prepHulkSerum();
			break;
		case ICE_CROWN:
			prepIceCrown();
			break;
		}
	}
	
	public void doActive(int whichActive, Point target) {
		switch(whichActive) {
		case FALCON_PUNCH: 
			falconPunch(target);
			break;
		case GRENADE_LAUNCHER:
			grenadeLauncher(target);
			break;
		case DRILL_DOZER:
			drillDozer(target);
			break;
		case HULK_SERUM:
			hulkOut();
			break;
		case ICE_CROWN:
			iceBlast(target);
			break;
		}
	}
	
	private void prepFalconPunch() {
		System.out.println("FALCONNNNNNNNNN...  (Choose a direction)");
		InGameState.inputState = InGameState.PLAYER_CHOOSE_DIR;
	}
	
	private void falconPunch(Point target) {
		System.out.println("PAWNCHHHHH in the direction of dx="+target.x+", dy="+target.y);
		mainChar.forceMarch(target.x*4, target.y*4);
		
		InGameState.waitOn(new Explosion(mainChar.getX(), mainChar.getY(), new Point(target.x*4, target.y*4)));
	}
	
	private void prepGrenadeLauncher() {
		System.out.println("You ready your grenade launcher (Choose a direction)");
		InGameState.inputState = InGameState.PLAYER_CHOOSE_DIR;
	}
	
	private void grenadeLauncher(Point target) {
		Grenade nade = new Grenade(mainChar.getX(), mainChar.getY(), target);
		InGameState.addPet(nade);
		InGameState.addEvent(new Event.Spawn(nade));
	}
	
	private void prepDrillDozer() {
		System.out.println("You rev up the engine on your Portable Drill Dozer (Choose a direction)");
		InGameState.inputState = InGameState.PLAYER_CHOOSE_DIR;
	}
	
	private void drillDozer(Point target) {
		DrillDozer dozer = new DrillDozer(mainChar.getX(), mainChar.getY(), target);
		InGameState.addPet(dozer);
		InGameState.addEvent(new Event.Spawn(dozer));
	}
	
	private void prepHulkSerum() {
		InGameState.newOngoingEffect(new OngoingHulkOut(10, mainChar));
		System.out.println("Your vision clouds with green as your body swells to grotesque proportions!");
		mainChar.currentGameState.playerTurnDone();
	}
	
	public void hulkOut() {
	
	}
	
	public void prepIceCrown() {
		System.out.println("You call upon the powers of the ice and snow... (Choose a direction)");
		InGameState.inputState = InGameState.PLAYER_CHOOSE_DIR;
	}
	
	public void iceBlast(Point target) {
		System.out.println("You bring forth a flurry of ice and snow!");
		InGameState.waitOn(new IceBlast(mainChar.x, mainChar.y, target));
	}
}
