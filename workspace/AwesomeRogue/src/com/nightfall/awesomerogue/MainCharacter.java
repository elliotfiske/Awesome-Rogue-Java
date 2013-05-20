package com.nightfall.awesomerogue;

import java.awt.Point;


public class MainCharacter extends Character {
	public static final int VISIONRANGE = 35;
	
	private int awesome;
	private Skill[] skills;
	
	public MainCharacter(int x, int y) {
		super(x, y, "@");
		awesome = 100;
		setCurrentWeapon(new Fists());
		skills = new Skill[3];
	}
	public int getAwesome() { return awesome; }
	public void findArtifact() {
		int randomArtifact = (int)Math.floor(Math.random()*Skill.allSkills.size());
		for(int i = 0; i < 3; i ++) {
			if(skills[i] == null) {
				skills[i] = Skill.allSkills.remove(randomArtifact);
				return;
			}
		}
		// We already have 3 skills! Need to replace and old one
		// TODO once we actually have 3 skills/levels
	}
	
	public void prepareSkill(int skill) {
		if(skills[skill] != null) {
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
		if(skills[skill] != null) {
			skills[skill].activate(p, this);
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
}
