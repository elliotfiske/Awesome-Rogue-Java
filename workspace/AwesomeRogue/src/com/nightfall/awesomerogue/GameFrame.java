package com.nightfall.awesomerogue;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


@SuppressWarnings("serial")
public class GameFrame extends JFrame implements WindowListener {
	public static final int DEFAULT_FPS = 60;
	private GamePanel gamePanel;
	
	public static void main(String[] args) {
		// Set FPS to default or user-given value
		int fps = DEFAULT_FPS;
		if(args.length != 0) fps = Integer.parseInt(args[0]);
		
		long period = (long) 1000.0/fps;
		
		Sprites.loadSprites();
		
		new GameFrame(period);
	}
	
	public GameFrame(long period) {
		gamePanel = new GamePanel(this, period);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		makeGUI(period);
	}
	
	/* put functions here that have to do with JTextFields and stuff
	 * if you ever add them to your game. You would initialize them
	 * when you call the constructor, and then make a function here
	 * to change them to whatever value.
	 */
	
	private void makeGUI(long period) {
	}
	
	/* The following functions are all implementations of WindowListener
	 * for stuff that is related to window management
	 */

	public void windowActivated(WindowEvent e) { gamePanel.resumeGame(); }
	
	public void windowClosing(WindowEvent e) { gamePanel.stopGame(); }

	public void windowClosed(WindowEvent e) { gamePanel.stopGame(); }

	public void windowDeactivated(WindowEvent e) { gamePanel.pauseGame(); }

	public void windowDeiconified(WindowEvent e) { gamePanel.resumeGame(); }

	public void windowIconified(WindowEvent e) { gamePanel.pauseGame(); }

	public void windowOpened(WindowEvent e) { }
	/* End window functions */
	
	public static BufferedImage[] loadAnimation(String filename, int imWidth) {
		BufferedImage stripIm;
		BufferedImage[] strip;

		try {
			stripIm = ImageIO.read(new File("img/"+filename));
			
			int height = stripIm.getHeight();
			
			int number = stripIm.getWidth() / imWidth;
			
			strip = new BufferedImage[number];
			
			for(int i=0; i < number; i ++) {
				strip[i] = new BufferedImage(imWidth, height, BufferedImage.TYPE_INT_ARGB);
				
				System.out.println("LOADED NUMBA " + i + " OF " + filename);
				
				Graphics2D stripGC = strip[i].createGraphics();
				stripGC.drawImage(stripIm, 0, 0, imWidth, height, i*imWidth, 0, (i*imWidth)+imWidth, height, null);
			}
			
			return strip;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static BufferedImage[] loadVertAnimation(String filename, int imHeight) {
		BufferedImage stripIm;
		BufferedImage[] strip;

		try {
			stripIm = ImageIO.read(new File("img/"+filename));
			
			int width = stripIm.getWidth();
			
			int number = stripIm.getHeight() / imHeight;
			
			strip = new BufferedImage[number];
			
			for(int i=0; i < number; i ++) {
				strip[i] = new BufferedImage(width, imHeight, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D stripGC = strip[i].createGraphics();
				stripGC.drawImage(stripIm, 0, 0, width, imHeight, 0, imHeight*i, width, (i*imHeight)+imHeight, null);
			}
			
			return strip;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
}
