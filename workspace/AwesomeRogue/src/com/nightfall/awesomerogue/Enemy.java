package com.nightfall.awesomerogue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Enemy extends Character {

	public static final int ANGRY_MUSHROOM = 0;
	public static final int RAT = 1;
	public static final int GIANT_RAT = 2;
	public static final int ZOMBIE = 3;
	public static final int SKELETON = 4;
	public static final int WIZARD = 5;
	public static final int MUSHROOM = 6;
	public static final int OGRE = 7;

	public static final String[] enemyIcons = {"M", "r", "R", "Z", "S", "W", "m"};

	private int whichEnemy, health = 0;
	String name;
	String icon;
	int weight;

	/** Enemies have a speed/5 chance of moving */
	int speed;
	
	/** An enemy is stuck in place until "stunned" is 0, decremented each turn. */
	int stunned;
	/** Frozen is like stunned, but bluuue */
	int frozen;

	private int xBounty, yBounty;
	private Tile bounty;
	
	/** smartMove stops if a enemy has been seen that WAS NOT seen before. If smartSeen is true
	 * then the enemy has been visible since smartMove began. */
	public boolean smartSeen;

	public Enemy /* number one */ (int x, int y, int whichEnemy) {
		super(x, y, enemyIcons[whichEnemy]);

		this.whichEnemy = whichEnemy;

		smartSeen = false;
		stunned = frozen = 0;
		
		switch(whichEnemy) {
		case ANGRY_MUSHROOM:
			health = 15;
			name = "angry mushroom";
			weight = 20;
			speed = 0;
			break;
		case MUSHROOM:
			health = 1;
			name = "mushroom";
			weight = 15;
			speed = 0;
			break;
		case RAT:
			health = 10;
			name = "rat";
			weight = 15;
			speed = 5;
			break;
		case ZOMBIE:
			health = 30;
			name = "zombie";
			weight = 30;
			speed = 3;
			break;
		case SKELETON:
			health = 40;
			name = "skeleton";
			weight = 10;
			speed = 4;
			break;
		case WIZARD:
			health = 50;
			name = "ALLAN PLEASE PUT IN WIZARD NAME";
			//TODO: implement sweet wizard name maker
			weight = 35;
			speed = 5;
			break;
		case OGRE:
			health = 20;
			name = "ogre";
			weight = 30;
			speed = 2;
			break;
		}

		super.character = name;
		
		icon = enemyIcons[whichEnemy];

		setCurrentWeapon(new EnemyWeapon(whichEnemy));
	}

	public void setBounty(int x, int y, Tile bountyTile) {
		xBounty = x;
		yBounty = y;
		bounty = bountyTile;
	}
	
	public int getType() { return whichEnemy; }

	public void move(int dx, int dy, Tile[][] map) {
		initPos(dx, dy);
		//sanity check here.
	}

	public void takeTurn(MainCharacter mainChar, Tile[][] map) {
		if(stunned > 0) {
			stunned--;
			return;
		}
		
		if(frozen > 0) {
			frozen--;
			return;
		}
		
		if(whichEnemy == RAT) {
			//Rats move randumbly
			if(Math.random() < 0.3) {
				moveRandomly(map);
			} else {
				pathToHeroAndMove(mainChar.getX(), mainChar.getY(), map);
			}
		} else {
			pathToHeroAndMove(mainChar.getX(), mainChar.getY(), map);
		}
	}

	/**
	 * Render the enemy to the screen.
	 * 
	 * @param g2 The Graphics2D handle it uses to draw itself.
	 * @param camX Camera X offset
	 * @param camY Camera Y offset
	 */
	public void draw(Graphics2D g2, int camX, int camY, int screenShake) {
		
		if(frozen > 0) {
			g2.setColor(Color.blue);
		} else {
			g2.setColor(Color.white);
		}
		
		g2.drawString(icon, ((x - camX) * InGameState.TILE_SIZE + 2),
				((y - camY) * InGameState.TILE_SIZE + 10 + InGameState.screenShake));	
	}

	/**
	 * Enemies have hit points (unlike the player's "Awesome level.")
	 * 
	 * This takes them away.  It also checks if they're dead.
	 * 
	 * @param damage How much damage to do to the monster.
	 */
	public void getHit(int damage, Tile[][] map, Character[][] entities) {
		health -= damage;
		InGameState.addEvent(new Event.DamageTaken(this, damage));
		
		if(health <= 0) {
			die();
			entities[x][y] = null;

			if(bounty != null) {
				map[xBounty][yBounty] = bounty;
			}

			if(whichEnemy == WIZARD) {
				//win
			}

			System.out.println("The " + name + " is slain!");

			//TODO!
			//InGameState.addEvent("killed" + getName() + "at" + x + "x" + y);
		}
	}
	
	public void stun(int turns) {
		if(stunned > turns) return;
		stunned = turns;
	}
	
	public void freeze(int turns) {
		if(frozen > turns) return;
		frozen = turns;
	}
	
	/**
	 * Heals an enemy. I'd like to implement some kind of enemy-healer, because
	 * those guys are so wonderfully annoying and it's super satisfying to
	 * mash their heads into the ground.
	 * 
	 * @param health How much healing?
	 */
	public void getHealed(int healing) {
		this.health += healing;
		
		InGameState.healText(x, y, healing, true);
	}

	public int getWeight() {
		return weight;
	}

	public void die() {
		super.die();
		InGameState.removeEnemy(this);
	}

	/**
	 * Move randomly. Rats occasionally do this, and maybe we'll have a "confused" status!
	 * @param map I'm the map, I'm the map, I'm the map, I'm the map, I'M THE MAP
	 */
	private void moveRandomly(Tile[][] map) {
		int randDirection = (int) Math.floor(Math.random() * 8);
		Point randPoint = Utility.getPointDirection(randDirection);
		int numTries = 0;
		
		while(map[x + randPoint.x][y + randPoint.y].isBlocker() && numTries < 12) {
			randDirection = (int) Math.floor(Math.random() * 8);
			randPoint = Utility.getPointDirection(randDirection);
			
			numTries++;
		}
		
		if(numTries < 12) {
			moveTo(x + randPoint.x, y + randPoint.y);
		}
	}
	
	//Fuzzy pathfinding = fun times for all!
	/**
	 * @param targetX
	 * @param targetY
	 * @param map
	 */
	public void pathToHeroAndMove(int targetX, int targetY, Tile[][] map) {
		//Grab a handle to the entities array
		Character[][] entities = InGameState.getEntities();
		
		//Try to path straight from the monster to the hero.

		//Coordinates of the line that walks to the player.
		Point straightPoint = new Point(x, y);

		/** List of tiles straight from the monster to the player. */
		ArrayList<Tile> straightTiles = new ArrayList<Tile>();
		
		boolean blocked = false;
		
		while(!(straightPoint.x == targetX && straightPoint.y == targetY)) {
			//Calculate which direction it would be smart to go in order to walk to the player.
			Utility.walkStraight(straightPoint, new Point(targetX, targetY), 3);

			straightTiles.add(new Tile( map[straightPoint.x][straightPoint.y].type , 0, straightPoint.x, straightPoint.y));

			if(map[straightPoint.x][straightPoint.y].isBlocker()) {
				blocked = true;
				//map[straightPoint.x][straightPoint.y].illustrate(Color.red);//TODO
				//if(entities[straightPoint.x][straightPoint.y] != null && entities[straightPoint.x][straightPoint.y].getName() == "Main Character")
					//map[straightPoint.x][straightPoint.y].illustrate(Color.green);
			} else {
				//map[straightPoint.x][straightPoint.y].illustrate(Color.yellow);// TODO
			}
		}

		//First off, check if the enemies even CAN move:
		double chanceOfMoving = speed / 5;
		if(chanceOfMoving < Math.random()) {
			return;
		}
		
		//TODO: Swarming mechanics
		

		//DEAL WITH OBSTACLES HERE

		/** Save the FIRST list of tiles we get.  This is because the enemy is looking to address the FIRST obstacle in his way,
		 * and only really cares that there does exist a path to you at the end. */
		ArrayList<Point> firstCorrectPath = null;

		for(int whichTile = 0; whichTile < straightTiles.size(); whichTile++) {
			Tile t = straightTiles.get(whichTile);
			Tile prevTile = null;

			//Ran into some nasty array index out of bounds exceptions don'cha know.
			if(whichTile == 0) {
				prevTile = map[x][y];
			} else {
				prevTile = straightTiles.get(whichTile - 1);
			}

			//Go through until we run into sexy trouble (blocker) or another enemy.
			//Also make sure that the tile PREVIOUS to this one is NOT a blocker (so we don't do two blockers in a row).
			if((t.isBlocker() && !prevTile.blocker)) {
				//OH NO! Blocker found.  Send out "feelers" to go along right and left walls.
				//Start feelers at the square on the straight-line path right BEFORE the wall.
				Point rightFeeler = new Point(prevTile.x, prevTile.y);
				Point leftFeeler = new Point(prevTile.x, prevTile.y);
				ArrayList<Point> leftPath = new ArrayList<Point>();
				ArrayList<Point> rightPath = new ArrayList<Point>();
				leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
				rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

				//Get started on feelin' things out.
				//Right:
				lastWallRight = getDirection(rightFeeler, new Point(t.x, t.y), true, map);

				//Left:
				lastWallLeft = getDirection(leftFeeler, new Point(t.x, t.y), false, map);

				if(lastWallRight.equals(new Point(0,0)) || lastWallLeft.equals(new Point(0,0))) {
					return;
				}

				Point lastRightFeeler = new Point(rightFeeler.x, rightFeeler.y);
				Point lastLeftFeeler = new Point(leftFeeler.x, leftFeeler.y);

				leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
				rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

				//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO
				//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

				int numTiles = 0;
				while(numTiles < 1000) {
					//follow right wall

					lastWallRight = getDirection(rightFeeler, lastWallRight, true, map);

					//map[rightFeeler.x][rightFeeler.y].illustrate(Color.blue); //TODO

					//follow left wall
					lastWallLeft = getDirection(leftFeeler, lastWallLeft, false, map);

					if(lastWallRight.equals(new Point(0,0)) || lastWallLeft.equals(new Point(0,0))) {
						return;
					}
					
					//map[leftFeeler.x][leftFeeler.y].illustrate(Color.cyan);

					leftPath.add(new Point(leftFeeler.x, leftFeeler.y));
					rightPath.add(new Point(rightFeeler.x, rightFeeler.y));

					//See if the PREVIOUS feeler and the CURRENT feeler cross over the straight tiles path.
					Point intersection = findIntersection(straightTiles, rightFeeler, lastRightFeeler, whichTile, map);
					if(!intersection.equals(new Point(-1, -1))) {
						if(firstCorrectPath == null) {
							firstCorrectPath = rightPath;
						}
						break;
					}

					intersection = findIntersection(straightTiles, leftFeeler, lastLeftFeeler, whichTile, map);
					if(!intersection.equals(new Point(-1, -1))) {
						if(firstCorrectPath == null) {
							firstCorrectPath = leftPath;
						}
						break;
					}

					//Store the previous feeler coordinates to make sure we don't accidentally let something slip
					//diagonally through.
					lastRightFeeler = new Point(rightFeeler.x, rightFeeler.y);
					lastLeftFeeler = new Point(leftFeeler.x, leftFeeler.y);

					numTiles++;
				}
				
				if(numTiles >= 999) {
					//No path found, I guess.
					break;
				}
			}
		}

		//The LAST AND FINAL thing this algorithm does is look for the best way to follow the twisty path we've just
		//laid out for the enemy.
		int proposedDX = 0;
		int proposedDY = 0;
		if(firstCorrectPath != null) {

			boolean weDidIt = false;
			for(int i = firstCorrectPath.size() - 1; i > 0; i--) {
				Point pointToCheck = firstCorrectPath.get(i);
				//map[pointToCheck.x][pointToCheck.y].illustrate(Color.pink); //TODO

				//If there's a straight, unblocked path to the pink tile we've just found our
				//route to the player.
				Point finalPath = new Point(x, y);

				ArrayList<Point> finalPathPoints = new ArrayList<Point>();
				finalPathPoints.add(new Point(finalPath.x, finalPath.y));

				/** The step we WOULD take to follow this new path is: */
				Point firstStep = new Point(finalPath.x, finalPath.y);
				Utility.walkStraight(firstStep, new Point(pointToCheck.x, pointToCheck.y), 3);

				//optimism!
				weDidIt = true;

				while(finalPath.x != pointToCheck.x || finalPath.y != pointToCheck.y) {
					Utility.walkStraight(finalPath, pointToCheck, 3);
					finalPathPoints.add(new Point(finalPath.x, finalPath.y));
					//map[finalPath.x][finalPath.y].illustrate(Color.black); //TODO
					if(map[finalPath.x][finalPath.y].isBlocker()) {
						//map[finalPath.x][finalPath.y].illustrate(Color.red); //TODO
						//Outta luck.  Try the next one!
						weDidIt = false;
						break;
					}
				}

				//straight path found! rejoice!
				if(weDidIt) {
					proposedDX = firstStep.x - x;
					proposedDY = firstStep.y - y;
					for(Point p : finalPathPoints) {
						//map[p.x][p.y].illustrate(Color.green); //TODO
					}
					//map[firstStep.x][firstStep.y].illustrate(Color.ORANGE); //TODO
					break;
				}
			}

			//Don't worry.  Just follow the feelers from before.
			if(!weDidIt) {

			}
		} else {
			//potential problem if the rat is inside of me
			if(straightTiles.size() == 0) {
				throw new PANICEVERYTHINGISBROKENERROR("THE " + getName() + " IS INSIDE ME! INSIIIIIIDE ME!!!");
			}
			
			//There must have been no obstacles.  Follow the straight path.
			proposedDX = straightTiles.get(0).x - x;
			proposedDY = straightTiles.get(0).y - y;
			//map[x + proposedDX][y + proposedDY].illustrate(Color.ORANGE);
		}

		if(entities[x + proposedDX][y + proposedDY] instanceof MainCharacter) {
			System.out.println("The rat scratches you!");
			entities[x+proposedDX][y+proposedDY].getHit(5, null, null);
		} else if(map[x + proposedDX][y + proposedDY].isBlocker()) {
			//We pathed into a wall.  Oh well.  Don't move!
		} else {

			moveTo(x + proposedDX, y + proposedDY);

			//System.out.println("Entity changed? Entity[x][y]: " + entities[x][y].getClass().getName() + " at " + x + ", " + y);
		}
	}	

	/**
	 * This method looks for an intersection between the straightTiles path and the feeler path.
	 * @param straightTiles The list of tiles that lead straight to the player.
	 * @param rightFeeler The "feeler" that was sent out along the right wall.
	 * @param lastRightFeeler The "feeler" that was sent out along the left wall.
	 * @param whichTile The spot in the straightTiles ArrayList that we know will move us forward.
	 * @param map A handle to the map.
	 * @return The Point they intersect, or (-1, -1) if they don't.
	 */
	public Point findIntersection(ArrayList<Tile> straightTiles,
			Point feeler, Point lastFeeler, int whichTile, Tile[][] map) {

		//3 points to check:
		// xp
		// fx
		//
		//"f", and both "x"s.

		//check the "f"
		if(straightTiles.indexOf(map[feeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][feeler.y]) != -1) {
			return new Point(feeler.x, feeler.y);
		}

		//check one "x"
		if(straightTiles.indexOf(map[feeler.x][lastFeeler.y]) > whichTile &&
				straightTiles.indexOf(map[feeler.x][lastFeeler.y]) != -1) {
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		//check other "x"
		if(straightTiles.indexOf(map[lastFeeler.x][feeler.y]) > whichTile &&
				straightTiles.indexOf(map[lastFeeler.x][lastFeeler.y]) != -1) {
			//still return the feeler position b/c we know it's on a floor tile.
			return new Point(feeler.x, feeler.y);
		}

		return new Point(-1, -1);
	}

	/**
	 * This method takes a feeler and makes it follow the right wall.
	 * 
	 * @param feeler The feeler that will be moved along the wall.
	 * @param lastWall The last wall that the feeler touched.
	 * @param goingRight True if we're following the right wall, false otherwise.
	 * @param map The array of Tiles.
	 * @return The last WALL the feeler touched. This is important to the wall-following algorithm.
	 */
	public Point getDirection(Point feeler, Point lastWall, boolean goingRight, Tile[][] map) {
		/*
		 * The algorithm starts by looking at the direction between the feeler and its last-touched wall:
		 *  _
		 * | |
		 * | |
		 * |_|F    (so here we'd start by looking at direction = 6).
		 *   
		 * 
		 * It then cycles around the possible direction values until it finds an empty square.
		 * If we're going right, it goes around clockwise, if we're going left, it goes around
		 * counter-clockwise.
		 * 
		 * So here, looking for the direction the right feeler should go results in:
		 *  _
		 * | |
		 * |x|R    (we checked the spot where the "x" is and it came up a wall, so we kept going 'round
		 * |_|F     clockwise until we hit the spot where R is)
		 *   
		 * And left:
		 *  _
		 * | |
		 * | |
		 * |_|F    (we checked the direction counter-clockwise and immediately found an empty tile, so we
		 *  L       go in the direction of the L.)
		 *  
		 */
		int diffX = 0, diffY = 0;

		diffX = lastWall.x - feeler.x;
		diffY = lastWall.y - feeler.y;

		int result = Utility.getNumberedDirection(new Point(diffX, diffY));
		if(result == -1) {
			//We're probably stuck in a crowd. Just chill.
			System.out.println("The guy at " + x + ", " + y + "doesn't like you.  Zooming in now:");

			return new Point(0,0);
		}

		//anti-infinity fail-safe
		int numTries = 0;
		while(numTries < 10) {
			//right feeler looks clockwise:
			if(goingRight) {
				result = (result + 1) % 8;
			} else {
				result = (result - 1);
				if(result < 0) {
					result = 7;
				}
			}

			diffX = Utility.getPointDirection(result).x;
			diffY = Utility.getPointDirection(result).y;

			//Outta bounds check!
			if(feeler.x + diffX < 0 || feeler.x + diffX >= map.length ||
					feeler.y + diffY < 0 || feeler.y + diffY >= map[0].length) {
				continue; //pretend it's a blocker.
			}

			if(!map[feeler.x + diffX][feeler.y + diffY].isBlocker()) {
				//We did it!
				//Grab the result of this function: the last-touched wall.
				int wallDirection = Utility.getNumberedDirection(new Point(diffX, diffY));

				//(it should be one cycle back).
				if(goingRight) {
					wallDirection = (wallDirection - 1);
					if(wallDirection < 0) {
						wallDirection = 7;
					}
				} else {
					wallDirection = (wallDirection + 1) % 8;
				}

				Point wallDiff = Utility.getPointDirection(wallDirection);
				Point lastWallTouched = new Point(feeler.x + wallDiff.x, feeler.y + wallDiff.y);

				//Move the feeler to the proper location:
				feeler.x += diffX;
				feeler.y += diffY;

				return lastWallTouched;
			}

			numTries++;
		}

		//We're probably stuck in a crowd. Just chill.
		System.out.println("The guy at " + x + ", " + y + "doesn't like you.  Zooming in now:");

		return new Point(0,0);
	}

	private Point lastWallLeft;
	private Point lastWallRight;
}
