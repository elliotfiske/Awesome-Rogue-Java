/****
 * Defines some effect that lasts longer than one turn.  For instance, the Hulking Out green overlay!
 * 
 * Can include an "initial effect" that usually serves as a transition into the effect, and an "outro" as
 * a transitino out.
 */
package com.nightfall.awesomerogue;

import java.awt.Graphics2D;

public abstract class OngoingEffect extends Effect {

	public OngoingEffect(String name) {
		super(name);
	}

	private Effect intro;
	private Effect outro;
	
	@Override
	public abstract void renderAndIterate(Graphics2D g2, Tile[][] map,
			Character[][] entities);

	@Override
	public abstract void render(Graphics2D g2);

	/** Tells what the OngoingEffect should do when a turn is taken. */
	public abstract void turnIterate(Tile[][] map, Character[][] entities);
	
	public Effect getIntro() {
		return intro;
	}

	public void setIntro(Effect intro) {
		this.intro = intro;
	}

	public Effect getOutro() {
		return outro;
	}

	public void setOutro(Effect outro) {
		this.outro = outro;
	}

}
