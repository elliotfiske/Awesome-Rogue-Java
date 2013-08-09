package com.nightfall.awesomerogue;

/****
 * Class Event covers all the possible events that can happen, and also has a way to undo them if need be.
 * 
 * Used for rewinding, could also be used for a "replay" feature in the future.
 */
public abstract class Event implements Undoable {
	//Declaring a bunch of static inner classes might be terrible Java practice, but I like not having too many files :I

	//Constants used by InGameState.addEvent()
	public static final int MOVEMENT = 0;
	public static final int HP_CHANGE = 1;
	
	/****
	 * Describes a movement FROM (oldX, oldY) to somewhere else.
	 * 
	 */
	public static class Movement extends Event {
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
	
	public static class DamageTaken extends Event {
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
	
	public static class MapChange extends Event {
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
	
	/**** JUST for enemies. Pets are murdered using Despawn. */
	public static class Murder extends Event { //isn't it nice to know Murder is undoable?
		Enemy victim;
		int healthBefore;
		
		public Murder(Enemy victim, int healthBefore) {
			this.victim = victim;
			this.healthBefore = healthBefore;
		}
		
		public void undo() {
			Enemy revivedEnemy = new Enemy(victim.x, victim.y, victim.getType());
			revivedEnemy.getHealed(healthBefore);
			InGameState.enemies.add(revivedEnemy);
		}
	}
	
	/**** Called when a pet is removed. */
	public static class Despawn extends Event {
		Pet pet;
		
		public Despawn(Pet pet) {
			this.pet = pet;
		}
		
		/** Rebirth! */
		public void undo() {
			pet.undoTurn();
		}
	}
	
	/**** Called when a new monster/pet is created */
	public static class Spawn extends Event {
		Character baby;
		
		public Spawn(Character baby) {
			this.baby = baby;
		}
		
		/** Slaughter. */
		public void undo() {
			if(baby instanceof Enemy) {
				InGameState.enemies.remove((Enemy) baby);
			} else {
				InGameState.pets.remove(baby);
			}
		}
	}
	
	/**** Calls "reverse()" on effects when they come up in the queue.  Gonna have to make a reverse of all the effects I think.
	 * WHOOO */
	public static class EffectHappened extends Event {

		Effect effect;
		
		public EffectHappened(Effect effect) {
			this.effect = effect;
		}
		
		public void undo() {
			InGameState.waitOn(effect);
			effect.reverse();
		}
	}
}
