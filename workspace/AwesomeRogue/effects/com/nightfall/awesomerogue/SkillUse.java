package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

public class SkillUse extends Effect {

	public String whichSkill;
	
	public SkillUse(String whichSkill) {
		super("Skill Direction Choose: " + whichSkill);
		this.whichSkill = whichSkill;
	}
	
	public void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities) {
		
	}

	public void render(Graphics2D g2) {
		
	}

	public void reverse() {
		//In general, skills are reversed just by reversing the effects that they cause.
		//Maybe there will be a skill that won't be able to work like that?
	}
}
