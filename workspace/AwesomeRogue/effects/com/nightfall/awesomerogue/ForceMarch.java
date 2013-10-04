package com.nightfall.awesomerogue;

import java.awt.Graphics2D;
import java.awt.Point;

public class ForceMarch extends Effect {

	Character mover;
	Point target;
	Point origin;
	/** How many milliseconds should elapse between each iteration of the effect? */
	int timeout;
	
	final static int COOLDOWN = 32;
	private int elapsedTime;
	
	public ForceMarch(Character mover, Point target) {
		super("Force March");
		this.mover = mover;
		this.target = target;
		
		origin = new Point(mover.x, mover.y);
		elapsedTime = 0;
		
		//Stun enemy for one turn, otherwise it gets to move RIGHT after getting forcemarched and it
		//looks weird
		if(mover instanceof Enemy) {
			System.out.println("Enemy force marched");
			((Enemy) mover).stun(1);
		}
	}

	public void iterate(long deltaTime) {
		//Check the cooldown
		elapsedTime += deltaTime;
		if(elapsedTime < COOLDOWN) {
			return;
		}
		elapsedTime = 0;
		
		Tile[][] map = InGameState.map;
		Character[][] entities = InGameState.getEntities();
		
		System.out.println("Force march iterated!");
		
		int proposedX = mover.x;
		int proposedY = mover.y;

		/** Rough estimate of speed based on how far they're gonna go 
		 * Uses Math.max to get the LARGETS of either X or Y movement, instead of adding them.*/
		int speed = Math.max(Math.abs(target.x - mover.x), Math.abs(target.y - mover.y));

		//Calculate how far we want to move!
		if(target.x < mover.x) {
			proposedX --;
		}
		else if(target.x > mover.x) {
			proposedX ++;
		}
		
		if(target.y < mover.y) {
			proposedY --;
		}
		else if(target.y > mover.y) {
			proposedY ++;
		}
		
		if(map[proposedX][proposedY].blocker) {
			//you hit a wall ouuuch
			if(mover instanceof MainCharacter) {
				System.out.println("You slam into a wall!");
			}

			if(mover instanceof Enemy) {
				System.out.println("The " + mover.getName() + " slams into a wall!");
			}

			setRunning(false);
			return;
		}

		if(entities[proposedX][proposedY] != null && entities[proposedX][proposedY] != mover) {
			//We just slammed into somebody.  LOOKS LIKE THEY'RE COMIN' ALONG FOR THE RIDE
			//First make sure it is actually possible to force march them (i.e. they're not being sandwiched by a wall)
			if(!entities[proposedX][proposedY].canForceMarch(new Point(proposedX - mover.x, proposedY - mover.y))) {
				setRunning(false);
				System.out.println("sandwich'd!!");
				return;
			} else {
				//Consider their weight.  Inelastic collision!
				int myWeight = mover.getWeight();
				int hisWeight = entities[proposedX][proposedY].getWeight();

				//If they're huge, they won't get knocked back as far.  Will never go below 1, though.
				int newSpeed = (int) (speed - Math.floor((double) hisWeight / (double) myWeight));
				if(newSpeed < 1) { newSpeed = 1; }

				//Now we gotta recalculate our target points.
				//First, reverse engineer the direction.
				Point direction = new Point(Utility.sign(target.x - mover.x), Utility.sign(target.y - mover.y));
				System.out.println("direction: " + direction);
				//Now, move the guy we ran into to this new target! (direction * speed)
				entities[proposedX][proposedY].forceMarch(direction.x * newSpeed, direction.y * newSpeed);
				//Meanwhile, adjust our target to one behind the other guy's.
				target = new Point(mover.x + direction.x * (newSpeed-1), mover.y + direction.y * (newSpeed-1));
				
				return;
			}
		}

		//Have we arrived at our destination?
		if(target.x == mover.x && target.y == mover.y) {
			//Feel free to move about the cabin
			setRunning(false);
			return;
		}

		//I guess we have no choice left but to move :P
		mover.forceMove(proposedX, proposedY);
	}

	public void render(Graphics2D g2) {
		//nothin' to render
	}

	public void reverse() {
		//Kinda hard to wrap my brain around.  Just reset to the original point, for now.
		mover.forceMove(origin.x, origin.y);
	}

	public String getName() {
		return "Force Marching " + mover.getName();
	}

}
