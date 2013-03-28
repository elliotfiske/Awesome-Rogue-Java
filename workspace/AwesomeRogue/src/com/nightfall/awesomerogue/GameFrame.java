package com.nightfall.awesomerogue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class GameFrame extends JFrame implements WindowListener {
	public static final int DEFAULT_FPS = 80;
	private GamePanel gamePanel;
	
	public static void main(String[] args) {
		// Set FPS to default or user-given value
		int fps = DEFAULT_FPS;
		if(args.length != 0) fps = Integer.parseInt(args[0]);
		
		long period = (long) 1000.0/fps;
		
		new GameFrame(period);
	}
	
	public GameFrame(long period) {
		gamePanel = new GamePanel(this, period);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		makeGUI(period);
	}
	
	/* put functions here that have to do with JTextFields and shit
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
}
