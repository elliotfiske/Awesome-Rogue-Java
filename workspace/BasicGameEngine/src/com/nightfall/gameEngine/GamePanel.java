package com.nightfall.gameEngine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable {
	public static final int PWIDTH = 800;
	public static final int PHEIGHT = 600;
	
	private Thread animator;
	private volatile boolean running = false;
	private volatile boolean isPaused = false;
	private volatile boolean gameOver = false;
	
	private volatile long period;
	
	// Private variables for off-screen rendering
	private Graphics dbg;
	private Image dbImage = null;
	
	// Holder of the frame
	private GameFrame gameFrame;
	
	// Holds GameStates
	private GameState currentGameState;
	
	public GamePanel(GameFrame frame, long period) {
		gameFrame = frame;
		this.period = period;
		
		setBackground(Color.black);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		gameFrame.setResizable(false);
		gameFrame.setContentPane(this);
		
		gameFrame.pack();
		gameFrame.setVisible(true);
		
		setFocusable(true);
		requestFocus();
		readyForTermination();
		
		// *******************************************************************************
		// *******************************************************************************
		// CHOOSE WHAT GAME STATE IS CREATED BY DEFAULT. THAT'S LITERALLY ALL YOU HAVE TO
		// DO WITH ANY OF THESE CLASSES IN ORDER FOR THE BASIC ENGINE TO WORK. ALL OF IT.
		// *******************************************************************************
		// *******************************************************************************
		currentGameState = new DefaultGameState(this);
		
		addMouseListener( new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{ testPress(e.getX(), e.getY()); }
		});
	} // End GamePanel()
	
	public void changeGameState(GameState gameState) {
		currentGameState = gameState;
	}
	
	private void readyForTermination() {
		addKeyListener( new KeyAdapter() {
			// Listen for esc and Ctrl+C
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if((keyCode == KeyEvent.VK_ESCAPE) ||
					(keyCode == KeyEvent.VK_END) ||
					((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
					running = false;
				}
				else {
					currentGameState.keyPress(e);
				}
			}
		});
	} // End readyForTermination
	
	// Make sure it is added to JFrame before starting
	public void addNotify() {
		super.addNotify();
		startGame();
	}
	
	private void startGame() {
		if(animator == null || !running) {
			animator = new Thread(this);
			animator.start();
		}
	} // End startGame()
	
	public void stopGame() {
		running = false;
	}
	
	public void pauseGame() {
		isPaused = true;
	}
	
	public void resumeGame() {
		isPaused = false;
	}
	
	private void testPress(int x, int y) {
		if(!isPaused && !gameOver) {
			// Do something!
		}
	}
	
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;
		long excess = 0L;
		beforeTime = System.currentTimeMillis();
		
		// update, render, sleep
		running = true;
		while(running) {
			gameUpdate();
			gameRender();
			paintScreen();
			
			afterTime = System.currentTimeMillis();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;
			
			if(sleepTime >= 0) {
				try {
					Thread.sleep(sleepTime);
				}
				catch(InterruptedException ex) {}
				overSleepTime = (System.currentTimeMillis() - afterTime) - sleepTime;
			}
			else {
				excess -= sleepTime; // Store excess time value
				overSleepTime = 0L;
				
				if(++noDelays >= 1) {
					Thread.yield(); // Give thread a chance to run
					noDelays = 0;
				}
			}
			
			beforeTime = System.currentTimeMillis();
			 /* if Frame animation takes too long, update game state without
			  * rendering it, to get updates/sec neared to FPS.
			  */
			int skips = 0;
			while((excess > period) && (skips < 5)) {
				excess -= period;
				gameUpdate();
				skips ++;
			}
		}
		
		System.exit(0);
	}
	
	private void gameUpdate() {
		if(!isPaused && !gameOver && currentGameState != null) {
			currentGameState.update();
		}
	}
	
	private void gameRender() {
		// draw frame to image buffer
		if(dbImage == null) { // Create buffer
			dbImage = createImage(PWIDTH, PHEIGHT);
			if(dbImage == null) {
				System.out.println("dbImage is null");
				return;
			}
			else {
				dbg = dbImage.getGraphics();
			}
		}
		
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		
		if(currentGameState != null)
			currentGameState.render(dbg);
	} // End gameRender()
	
	private void paintScreen() {
		// Render buffer image to screen
		Graphics g;
		try {
			g = this.getGraphics();
			if((g != null) && (dbImage != null)) {
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync(); // Sync display on some systems
			g.dispose();
		}
		catch(Exception e) {
			System.out.println("Graphics context error: "+e);
		}
	}
}
