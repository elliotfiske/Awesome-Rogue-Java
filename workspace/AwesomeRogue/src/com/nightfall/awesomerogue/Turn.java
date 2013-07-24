package com.nightfall.awesomerogue;

import java.util.ArrayList;

import com.nightfall.awesomerogue.Event.Movement;
import com.nightfall.awesomerogue.Event.*;

/****
 * Represents a full "turn" of events, including movements, damagings, and effects (maybe?)
 */
public class Turn {
	/** Stores all the things that happened on this turn. */
	private ArrayList<Event> happenings;
	
	public Turn() {
		happenings = new ArrayList<Event>();
	}
	
	public void addEvent(Event event) {
		happenings.add(event);
	}
	
	/** Gets the last event that happened and removes it from the stack. */
	public Event getLastEvent() {
		Event result = happenings.get(happenings.size() - 1);
		happenings.remove(result);
		return result;
	}
}
