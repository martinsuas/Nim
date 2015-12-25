//******************************************************************************
//
// File:    NimUI.java
// Package: ---
// Unit:    Class NimUI.java
//
// This Java source file is copyright (C) 2015 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 3 of the License, or (at your option) any
// later version.
//
// This Java source file is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You may obtain a copy of the GNU General Public License on the World Wide Web
// at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Class NimUI provides the user interface for the Nim network game.
 *
 * @author  Alan Kaminsky
 * @version 07-Oct-2015
 */
public class NimUI
	implements ModelListener {
// Interface for a listener for HeapPanel events.

	private static interface HeapListener
		{
		// Report that markers are to be removed from a heap.
		public void removeObjects
			(int id,          // Heap panel ID
			 int numRemoved); // Number of markers to be removed
		}

// Class for a Swing widget displaying a heap of markers.

	private static class HeapPanel
		extends JPanel
		{
		private static final int W = 50;
		private static final int H = 30;
		private static final Color FC = Color.RED;
		private static final Color OC = Color.BLACK;

		private int id;
		private int maxCount;
		private int count;
		private boolean isEnabled;
		private HeapListener listener;

		// Construct a new heap panel.
		public HeapPanel
			(int id,       // Heap panel ID
			 int maxCount) // Maximum number of markers
			{
			this.id = id;
			this.maxCount = maxCount;
			this.count = maxCount;
			this.isEnabled = true;
			Dimension dim = new Dimension (W, maxCount*H);
			setMinimumSize (dim);
			setMaximumSize (dim);
			setPreferredSize (dim);
			addMouseListener (new MouseAdapter()
				{
				public void mouseClicked (MouseEvent e)
					{
					if (isEnabled && listener != null)
						{
						int objClicked = maxCount - 1 - e.getY()/H;
						int numRemoved = count - objClicked;
						if (numRemoved > 0)
							listener.removeObjects (id, numRemoved);
						}
					}
				});
			}

		// Set this heap panel's listener.
		public void setListener
			(HeapListener listener)
			{
			this.listener = listener;
			}

		// Set the number of markers in this heap panel.
		public void setCount
			(int count) // Number of markers
			{
			count = Math.max (0, Math.min (count, maxCount));
			if (this.count != count)
				{
				this.count = count;
				repaint();
				}
			}

		// Enable or disable this heap panel.
		public void setEnabled
			(boolean enabled) // True to enable, false to disable
			{
			if (this.isEnabled != enabled)
				{
				this.isEnabled = enabled;
				repaint();
				}
			}

		// Paint this heap panel.
		protected void paintComponent
			(Graphics g) // Graphics context
			{
			super.paintComponent (g);

			// Clone graphics context.
			Graphics2D g2d = (Graphics2D) g.create();

			// Turn on antialiasing.
			g2d.setRenderingHint
				(RenderingHints.KEY_ANTIALIASING,
				 RenderingHints.VALUE_ANTIALIAS_ON);

			// For drawing markers.
			Ellipse2D.Double ellipse = new Ellipse2D.Double();
			ellipse.width = W - 2;
			ellipse.height = H - 2;
			ellipse.x = 1;

			// If enabled, draw filled markers.
			if (isEnabled)
				{
				g2d.setColor (FC);
				for (int i = 0; i < count; ++ i)
					{
					ellipse.y = (maxCount - 1 - i)*H + 1;
					g2d.fill (ellipse);
					}
				}

			// If disabled, draw outlined markers.
			else
				{
				g2d.setColor (OC);
				for (int i = 0; i < count; ++ i)
					{
					ellipse.y = (maxCount - 1 - i)*H + 1;
					g2d.draw (ellipse);
					}
				}
			}
		}

// Hidden data members.

	private static final int NUMHEAPS = 3;
	private static final int NUMOBJECTS = 5;
	private static final int GAP = 10;
	private static final int COL = 10;

	private JFrame frame;
	private HeapPanel[] heapPanel;
	private JTextField myNameField;
	private JTextField theirNameField;
	private JTextField whoWonField;
	private JButton newGameButton;
	
	private int playerID, opponentID;
	private String playerName, opponentName;
	private int playerScore, opponentScore;
	
	private ViewListener viewListener;

// Hidden constructors.

	/**
	 * Construct a new Nim UI.
	 * @param  name      Name of the player.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private NimUI
		(String name)
		{
		frame = new JFrame ("Nim -- " + name);
		JPanel panel = new JPanel();
		panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
		frame.add (panel);
		panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));

		heapPanel = new HeapPanel [NUMHEAPS];
		for (int h = 0; h < NUMHEAPS; ++ h)
			{
			panel.add (heapPanel[h] = new HeapPanel (h, NUMOBJECTS));
			panel.add (Box.createHorizontalStrut (GAP));
			heapPanel[h].setListener( new HeapListener() 
				// Create new anonymous class
				{
				public synchronized void removeObjects( int id, int num_rem ) 
					{
						take( id, num_rem );
					}
				}
				);
			}

		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout (new BoxLayout (fieldPanel, BoxLayout.Y_AXIS));
		panel.add (fieldPanel);

		myNameField = new JTextField (COL);
		myNameField.setEditable (false);
		myNameField.setHorizontalAlignment (JTextField.CENTER);
		myNameField.setAlignmentX (0.5f);
		fieldPanel.add (myNameField);
		fieldPanel.add (Box.createVerticalStrut (GAP));

		theirNameField = new JTextField (COL);
		theirNameField.setEditable (false);
		theirNameField.setHorizontalAlignment (JTextField.CENTER);
		theirNameField.setAlignmentX (0.5f);
		fieldPanel.add (theirNameField);
		fieldPanel.add (Box.createVerticalStrut (GAP));

		whoWonField = new JTextField (COL);
		whoWonField.setEditable (false);
		whoWonField.setHorizontalAlignment (JTextField.CENTER);
		whoWonField.setAlignmentX (0.5f);
		fieldPanel.add (whoWonField);
		fieldPanel.add (Box.createVerticalStrut (GAP));

		newGameButton = new JButton ("New Game");
		newGameButton.setAlignmentX (0.5f);
		newGameButton.setFocusable (false);
		fieldPanel.add (newGameButton);
		
		// Default initial view
		for ( int h = 0; h < NUMHEAPS; h++ ) {
			heapPanel[h].setCount( NUMOBJECTS - NUMHEAPS + h + 1 );
			heapPanel[h].setEnabled(false);
		}
		newGameButton.setEnabled( false );
		
		// Clicking the newGameButton will reset the game	
		newGameButton.addActionListener( new ActionListener() {
				public void actionPerformed (ActionEvent e) {
					doButtonClick( e );
				}
		} );
		
		// Closing the window send a message to the server and closes
		frame.addWindowListener (new WindowAdapter()
			{
			public void windowClosing (WindowEvent e)
				{
					close();
				}
			});
		
		frame.pack();
		frame.setVisible (true);
		}

// Exported operations.

	/**
	 * An object holding a reference to a Nim UI.
	 */
	private static class UIRef
		{
		public NimUI ui;
		}

	/**
	 * Construct a new Nim UI.
	 */
	public static NimUI create
		(String name)
		{
		final UIRef ref = new UIRef();
		onSwingThreadDo (new Runnable()
			{
			public void run()
				{
				ref.ui = new NimUI (name);
				}
			});
		return ref.ui;
		}
	
	/**
	 * Set the view listener object for this class.
	 *
	 * @param  modelListener  Model listener.
	 */
	public synchronized void setViewListener( ViewListener vl ) {
		this.viewListener = vl;
	}
	
	/**
	 * Report the player's id
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void id( int i ) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					playerID = i;
				}
		});
	}
	
	/**
	 * Report the identity of a player
	 * @param  i      Player id
	 * @param  n      Player name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void name(int i, String n) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					if ( i == playerID ) {
						playerName = n;
					} else {
						opponentID = i;
						opponentName = n;
						newGameButton.setEnabled( true );
					}
				}
		});
	}
	
	/**
	 * Report the score of a player
	 * @param  i      Player id
	 * @param  s      Score
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void score(int i, int s) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					if ( i == playerID ) {
						myNameField.setText( playerName + " = " + s );
					}
					else {
						theirNameField.setText( opponentName + " = " + s );
					}
				}
		});
	}
	
	/**
	 * Report the state of a heap ( the number of markers it has
	 * left.
	 * @param  h      Heap id
	 * @param  m      Number of markers
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void heap(int h, int m) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					whoWonField.setText( "" );
					heapPanel[h].setCount( m );
				}
		});
	}
	
	/**
	 * Report who's turn it is
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void turn(int i) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					for ( int h = 0; h < NUMHEAPS; h++) {
						if (i == playerID )
							heapPanel[h].setEnabled(true);
						else
							heapPanel[h].setEnabled(false);
					}
				}
		});
	}
	
	/**
	 * Report a player has won
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void win(int i) {
		onSwingThreadDo (new Runnable() {
				public void run() {
					String winner;
					if (i == playerID) 
						winner = playerName;
					else
						winner = opponentName;
					whoWonField.setText( winner + " wins!");
				}
		});
	}
	
	/**
	 * Report the game has ended.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void quit() {
		onSwingThreadDo (new Runnable() {
				public void run() {
					System.exit (0);
				}
		});
	}	
		
// Hidden operations.
	/**
	 * Execute the given runnable object on the Swing thread.
	 * @param  id	      Player id
	 * @param  num_rem	  Number to take
	 */
	private synchronized void take( int id, int num_rem ) {
		try {
		viewListener.take( id, num_rem );
		}
		catch (IOException exc) {}
	}
	
	/**
	 * Closes the windows and quits the game.
	 */
	private synchronized void close() {
		try {
		viewListener.quit();
		}
		catch (IOException exc) {}
		System.exit (0);
	}
	
	/**
	 * Execute the given runnable object on the Swing thread.
	 * @param  e      Event
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private synchronized void doButtonClick(ActionEvent e) {
		try {
			viewListener.newGame();
		} catch (IOException exc) {}
	}
	
	/**
	 * Execute the given runnable object on the Swing thread.
	 * @param  task      Task to be ran
	 * @exception  Throwable
	 *     Thrown if an error occurred.
	 */
	private static void onSwingThreadDo
		(Runnable task)
		{
		try
			{
			SwingUtilities.invokeAndWait (task);
			}
		catch (Throwable exc)
			{
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}
	}
