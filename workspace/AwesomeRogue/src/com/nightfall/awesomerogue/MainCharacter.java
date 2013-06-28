package com.nightfall.awesomerogue;

import java.awt.Point;


public class MainCharacter extends Character {
	public static final int VISIONRANGE = 35;
	
	private int awesome;
	/** Array containing a list of the actives you've gotten so far */
	private int[] skills;
	private int numSkills;
	
	private InGameState currentGameState;
	private Active actives; //Our handle to the actives.
	
	/** Passives kind of works backwards from how skills work, since you can have ANY
	 * number of passives.  You check to see if you have a passive by calling passives[int passiveId],
	 * whereas skills[] just contains a list of the id's of the actives you've gathered. */
	private boolean[] passives;	
	
	public MainCharacter(int x, int y) {
		super(x, y, "@");
		awesome = 100;
		setCurrentWeapon(new Pistol());
		skills = new int[4];
		passives = new boolean[Passive.NUM_PASSIVES];
	}
	
	public int getAwesome() { return awesome; }
	
	public void findArtifact() {
		//For now just add a random skill I guess?
		int randomArtifact = (int)Math.floor(Math.random()*Skill.allSkills.size());
		for(int i = 0; i < 3; i ++) {
			if(skills[i] == Active.EMPTY_SLOT) {
				skills[i] = randomArtifact;
				return;
			}
		}
		// We already have 3 skills! Need to replace and old one
		// TODO once we actually have 3 skills/levels
	}
	
	public void getPassive(int whichPassive) {
		passives[whichPassive] = true;
	}
	
	public void setLevel(InGameState level) {
		currentGameState = level;
	}
	
	public InGameState getLevel() { return currentGameState; }
	
	public void prepareSkill(int skill) {
		if(skills[skill] != Active.EMPTY_SLOT) {
			// Prepare the skill. If it fires as well, it will return false
			// So we don't want to say to wait on it
			if(skills[skill].prepare()) {
				switch(skill) {
				case 0:
					InGameState.waitOn("Z");
					break;
				case 1:
					InGameState.waitOn("X");
					break;
				case 2:
					InGameState.waitOn("C");
					break;
				}
			}
		}
	}
	
	public void activateSkill(int skill, Point p) {
		if(skills[skill] != Active.EMPTY_SLOT) {
			actives.doActive(skills[skill]);
			switch(skill) {
			case 0:
				InGameState.endWait("Z");
				break;
			case 1:
				InGameState.endWait("X");
				break;
			case 2:
				InGameState.endWait("C");
				break;
			}
		}
	}
	
	//Overrides the move() method (only really matters if we're debugging and want to walk through walls.)
	public void move(int dx, int dy, Tile[][] map, Character[][] entities) {
		if(InGameState.GODMODE_WALKTHRUWALLS) {
			initPos(getX() + dx, getY() + dy);
		} else {
			super.move(dx, dy, map, entities);
		}
	}

	public boolean hasPassive(int passive) {
		return passives[passive] || InGameState.EVERY_PASSIVE_UNLOCKED;
	}
	
	public int howManySkills() {
		return numSkills;
	}
}
