package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public abstract class Skill {
	public static ArrayList<Object> allSkills;
	
	private int cooldown, maxCooldown;
	
	public Skill(int cooldown) {
		maxCooldown = cooldown;
		this.cooldown = cooldown;
	}
	
	public static void addSkills() {
		allSkills = new ArrayList<Object>();
		allSkills.add(new FalconPunch());
		allSkills.add(new GrenadeLauncher());
	}
	
	// Returns false if it prepares and activates in the same function
	// So we don't wait on it
	public abstract boolean prepare();
	public abstract void activate(Point direction, MainCharacter mainChar);
	public abstract void update();
	public abstract void draw(Graphics2D g2);


	public int getCooldown() { return cooldown; }
	public void setCooldown(int cooldown) { this.cooldown = cooldown; }
	public int getMaxCooldown() { return maxCooldown; }
	public void setMaxCooldown(int maxCooldown) { this.maxCooldown = maxCooldown; }
	
	
}