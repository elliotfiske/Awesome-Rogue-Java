package com.nightfall.awesomerogue;

import java.awt.image.BufferedImage;

public class LevelAnim {
	public static final int FRAMELEN = 15;
	
	private BufferedImage[] imgList;
	private int index;
	private boolean willReverse;
	
	/** Public constructor
	 * 
	 * @param string - The level name
	 * @param reverse - Whether or not to reverse when drawing (as opposed to cycle)
	 */
	public LevelAnim(String string, boolean reverse) {
		index = 0;
		willReverse = reverse;
		
		imgList = GameFrame.loadVertAnimation("metagame/"+string+"MetaAnim.png", 120);
	}
	
	public BufferedImage next() {
		// Decide what image to send back before incrementing the index
		BufferedImage img = (index <= 0) ? imgList[(index/FRAMELEN)*-1] : imgList[(index/FRAMELEN)];
		
		index ++;
		if(index >= imgList.length*FRAMELEN) {
			if(willReverse) index = imgList.length*-1*FRAMELEN + 1; // Make the index negative if we're reversing
			else index = 0;									// Otherwise start over at 0
		}
		
		return img;
	}
}
