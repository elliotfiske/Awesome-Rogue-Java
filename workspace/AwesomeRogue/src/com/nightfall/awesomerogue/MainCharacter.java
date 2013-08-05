package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;


public class MainCharacter extends Character {
	public static final int VISIONRANGE = 35;

	private int awesome;
	private int health;
	/** Array containing a list of the actives you've gotten so far */
	private int[] skills;
	private int numSkills;
	
	/** You've really let yourself go, main character.*/
	private int weight = 50;

	private InGameState currentGameState;
	private Active actives; //Our handle to the actives.

	/** Passives kind of works backwards from how skills work, since you can have ANY
	 * number of passives.  You check to see if you have a passive by calling passives[int passiveId],
	 * whereas skills[] just contains a list of the id's of the actives you've gathered. */
	private boolean[] passives;

	/** MainCharacter needs a map or he'll get lost */
	private Tile[][] map;
	
	/** HULK SMASH??!? */
	private boolean isHulking;	
	
	/** Keeps track of the skill we're currently waiting on. */
	private SkillUse skillInUse;

	public MainCharacter(int x, int y, Tile[][] map) {
		super(x, y, "@");
		awesome = 100;
		health = 200;
		setCurrentWeapon(new Pistol());
		skills = new int[4];
		passives = new boolean[Passive.NUM_PASSIVES];
		
		this.map = map;
		
		actives = new Active(this);
		
		skills[0] = Active.HULK_SERUM;
		skills[1] = Active.GRENADE_LAUNCHER;
		skills[2] = Active.DRILL_DOZER;
	}
	
	public MainCharacter(int x, int y) {
		this(x, y, null);
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
			// Prepare the skill.  This prompts the user for a direction and suspends the game.
			//When the user hits a direction, InGameState will call the Active half of the skill.
			actives.prepareActive(skills[skill]);
			
			switch(skill) {
			case 0:
				skillInUse = new SkillUse("Z");
				InGameState.waitOn(skillInUse);
				break;
			case 1:
				skillInUse = new SkillUse("X");
				InGameState.waitOn(skillInUse);
				break;
			case 2:
				skillInUse = new SkillUse("C");
				InGameState.waitOn(skillInUse);
				break;
			}
		} else {
			System.out.println("You don't have that skill yet...");
		}
	}

	public void activateSkill(int skill, Point target) {
		//Before if you hit a key that wasn't a direction you would drop a grenade on your feet.
		if(target.equals(new Point(0,0))) {
			return;
		}
		
		if(skills[skill] != Active.EMPTY_SLOT) {
			actives.doActive(skills[skill], target);
			InGameState.endWait(skillInUse);
		}
	}

	//Overrides the move() method (so I can be hulk)
	public void move(int dx, int dy, Tile[][] map, Character[][] entities) {
		if(InGameState.GODMODE_WALKTHRUWALLS) {
			initPos(getX() + dx, getY() + dy);
		} else if(isHulking) {
			//HULK SMASH THROUGH WALLS unless they are impassable
			//Check all the walls where we will be occupying.
			boolean canMove = true;
			ArrayList<Point> walls = new ArrayList<Point>();
			ArrayList<Point> doors = new ArrayList<Point>();
			for(int offsetX = -1; offsetX <= 1; offsetX++) {
				for(int offsetY = -1; offsetY <= 1; offsetY++) {
					if(map[getX() + dx + offsetX][getY() + dy + offsetY].type == Tile.WALL) {
						walls.add(new Point(getX() + dx + offsetX, getY() + dy + offsetY));
					}
					
					if(map[getX() + dx + offsetX][getY() + dy + offsetY].type == Tile.DOOR) {
						map[getX() + dx + offsetX][getY() + dy + offsetY] = new Tile(Tile.FLOOR, getX() + dx, getY() + dy);
						doors.add(new Point(getX() + dx + offsetX, getY() + dy + offsetY));
					}
					
					if(map[getX() + dx + offsetX][getY() + dy + offsetY].type == Tile.IMPASSABLE) {
						canMove = false;
						System.out.println("You pound your fists against the wall in vain; it won't budge.");
					}
					
					//Knock away anything in my way.
					Character victim = entities[getX() + dx + offsetX][getY() + dy + offsetY];
					if(victim != null && !(victim instanceof MainCharacter)) { //I kept hitting myself around the room.  It was pretty funny, though.
						knockAway(victim, 7);
					}
				}
			}
			
			if(canMove) {
				String wallString = "";
				String doorString = "";
				String conjunctionJunction = "";
				if(walls.size() == 1)  wallString = "wall";
				if(walls.size() > 1)   wallString = "walls";
				if(doors.size() == 1)  doorString = "door";
				if(doors.size() > 1)   doorString = "doors";
				if(walls.size() > 0 && doors.size() > 0) conjunctionJunction = " and the ";
				
				if(walls.size() > 0 || doors.size() > 0) {
					System.out.println("You smash through the " + wallString + conjunctionJunction + doorString + "!");
				}
				
				for(Point p : walls) {
					map[p.x][p.y] = new Tile(Tile.FLOOR, p.x, p.y);
				}
				
				for(Point p : doors) {
					map[p.x][p.y] = new Tile(Tile.FLOOR, p.x, p.y);
				}
				
				initPos(getX() + dx, getY() + dy);
			}
			
		} else {
			int targetX = x + dx;
			int targetY = y + dy;
			
			if(!map[targetX][targetY].blocker && entities[targetX][targetY] == null) {
				moveTo(targetX, targetY);
				room = map[x][y].room;
			}
			else if(entities[targetX][targetY] == null) {
				// Do action for the tile you tried to walk to.
				// That way we can have impassable tiles that
				// Can be interacted with.
				// Only do action if there's no enemy there though.
				map[targetX][targetY].doAction(this);
			}
			else if(entities[targetX][targetY] instanceof Enemy){
				attack(new Point(dx, dy));
			} else {
				System.out.println("Your " + entities[targetX][targetY].getName() + " is in the way.");
			}
		}
	}

	public void attack(Point direction) {
		
		if(isHulking) {
			Character[][] entities = InGameState.getEntities();
			System.out.println("HULK SMASH!!");
			for(int offsetX = -2; offsetX <= 2; offsetX++) {
				for(int offsetY = -2; offsetY <= 2; offsetY++) {
					Point spaceToCheck = new Point(getX() + offsetX, getY() + offsetY);
					if(entities[spaceToCheck.x][spaceToCheck.y] != null && !(entities[spaceToCheck.x][spaceToCheck.y] instanceof MainCharacter) ) {
						knockAway(entities[spaceToCheck.x][spaceToCheck.y], 7);
					}
				}
			}
		} else {
			super.attack(direction);
		}
	}
	
	public boolean hasPassive(int passive) {
		return passives[passive] || InGameState.EVERY_PASSIVE_UNLOCKED;
	}

	public int howManySkills() {
		return numSkills;
	}
	
	@Override
	public void draw(Graphics2D g2, int camX, int camY) {
		if(!isHulking) {
			super.draw(g2, camX, camY);
		} else {
			//TODO: replace with big green @
			g2.setColor(Color.green);
			g2.fillRect((getX()-camX)*12 - 12, ((getY()-camY)*12 - 12), InGameState.TILE_SIZE * 3, InGameState.TILE_SIZE * 3);
		}
	}

	public void setHulking(boolean willBeHulking) {
		
		if(isHulking && !willBeHulking) {
			//SHRIIINK
			isHulking = false;
			weight = 20;
		}
		
		if(!isHulking && willBeHulking) {
			//TRANSFOOOORM
			isHulking = true;
			
			//GAINS
			weight = 100;
			
			Character[][] entities = InGameState.getEntities();
			
			//knock down any walls around you
			boolean blewStuffUp = false;
			for(int dx = -1; dx <= 1; dx++) {
				for(int dy = -1; dy <= 1; dy++) {
					if(map[getX() + dx][getY() + dy].type == Tile.WALL) {
						map[getX() + dx][getY() + dy] = new Tile(Tile.FLOOR, getX() + dx, getY() + dy);
						blewStuffUp = true;
					}
					
					if(entities[getX() + dx][getY() + dy] != null) {
						knockAway(entities[getX() + dx][getY() + dy], 5);
					}
				}
			}
			
			if(blewStuffUp) {
				System.out.println("You smash down the walls around you as you transform!");
			}
		}
	}
	
	public boolean isHulking() {
		return isHulking;
	}

	public void giveMap(Tile[][] inmap) {
		this.map = inmap;
		System.out.println("MAP RECEIVED");
		Tile wat = map[20][20];
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void moveTo(int newX, int newY, Character[][] entities) {
		super.moveTo(newX, newY, entities);
		currentGameState.updateCamera();
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void addAwesome(int awesome) {
		this.awesome += awesome;
		currentGameState.awesomeText(x , y , awesome);
	}

	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		System.out.println("YOUCH you take " + damage + " damage!");
		//Floatytext handled in InGameState
		health -= damage;
		InGameState.addEvent(new Event.DamageTaken(this, damage));
	}
	
	public void getHealed(int amount) {
		health += amount;
		
		InGameState.healText(x, y, amount, false);
	}
	
	public String getName() {
		return "Main Character";
	}
}



