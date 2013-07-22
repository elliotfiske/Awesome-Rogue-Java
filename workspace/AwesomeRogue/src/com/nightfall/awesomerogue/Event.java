package com.nightfall.awesomerogue;

import java.util.ArrayList;

/****
 * Represents a full "turn" of events, including movements, damagings, and effects (maybe?)
 */
public class Event {
	private ArrayList<Movement> movements;
	
	
	public Event() {
		movements = new ArrayList<Movement>();
	}
	
	private class Movement extends Event implements Undoable {
		Character mover;
		int oldX, oldY, newX, newY;
		
		public Movement(Character mover, int oldX, int oldY, int newX, int newY) {
			this.mover = mover;
			this.oldX = oldX;
			this.oldY = oldY;
			this.newX = newX;
			this.newY = newY;
		}
		
		public void undo() {
			mover.moveTo(oldX, oldY);
		}
	}
	
	private class DamageTaken extends Event implements Undoable {
		Character victim;
		int amount;
		
		public DamageTaken(Character victim, int amount) {
			this.victim = victim;
			this.amount = amount;
		}
		
		public void undo() {
			//
		}
	}
	
	private interface Undoable {
		public void undo();
	}
}
