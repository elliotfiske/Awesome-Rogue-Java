package com.nightfall.awesomerogue;

/****
 * Class Event covers all the possible events that can happen, and also has a way to undo them if need be.
 * 
 * Used for rewinding, could also be used for a "replay" feature in the future.
 */
public class Event {
	//Declaring a bunch of static inner classes might be terrible Java practice, but I like not having too many files :I

	//Constants used by InGameState.addEvent()
	public static final int MOVEMENT = 0;
	public static final int HP_CHANGE = 1;
	
	/****
	 * Describes a movement FROM (oldX, oldY) to somewhere else.
	 * 
	 */
	public static class Movement extends Event implements Undoable {
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
	
	public static class DamageTaken extends Event implements Undoable {
		Character victim;
		int amount;
		
		public DamageTaken(Character victim, int amount) {
			this.victim = victim;
			this.amount = amount;
		}
		
		/**
		 * Hurts if you were healed, heals if you were hurt.
		 */
		public void undo() {
			if(amount > 0) {
				victim.getHealed(amount);
			} else {
				victim.getHit(amount, null, null);
			}
		}
	}
	
	public static class MapChange extends Event implements Undoable {
		Tile oldTileState, newTileState;
		
		/** Remember to deep copy you java programmer you */
		public MapChange(Tile oldTileState, Tile newTileState) {
			this.oldTileState = oldTileState;
			this.newTileState = newTileState;
		}

		public void undo() {
			InGameState.map[oldTileState.x][oldTileState.y] = oldTileState;
		}
	}
	
	private interface Undoable {
		public void undo();
	}
}
