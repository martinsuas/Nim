import java.io.IOException;

/**
 * Provides server-side model object in Nim game.
 *
 * @author  Martin Suarez
 * @version 12/02/2015
 */
public class NimModel implements ViewListener {
	
	// Hidden data members.
	
	private ModelListener player1, player2;
	private int id1, id2;
	private int score1, score2;
	private String name1, name2;
	private int current_turn_id;
	private int[] markers;
	
	// Hidden constructors
	/**
	 * Construct new Nim model.
	 */
	public NimModel() {
		markers = new int[3];
		markers[0] = 3;
		markers[1] = 4;
		markers[2] = 5;
	}
	
	// Exported operations
	/**
	 * Assigns one of the players. 
	 * @param 	ml		Model listener
	 * @param	is_p1	True if player1, false if player2.
	 * @param   name 	Name of player being added
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void addPlayer( ModelListener ml, boolean is_p1,
									    String name) throws IOException {
		try {
			// If creating player 1	
			if (is_p1) {
				player1 = ml;
				id1 = 1;
				score1 = 0;
				name1 = name;
				player1.id( id1 );
				player1.name( id1, name1 );
				player1.score(id1, score1);
			} 
			// If creating player 2
			else {
				player2 = ml;
				id2 = 2;
				score2 = 0;
				name2 = name;
				current_turn_id = id1;
				player2.id( id2 );
				player2.name( id2, name2 );
				player2.score(id2, score2);
				player1.name( id2, name2 );
				player2.name( id1, name1 );
				player1.score( id2, score2);
				player2.score( id1, score1);
				player1.turn( current_turn_id );
				player2.turn( current_turn_id );
			}
		} catch (IOException exc) {
		}
	}
	
	/**
	 * Checks if missing a player.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized boolean needsPlayer( ) {
		boolean result = true;
		if (player2 == null) {
			result = true;
		} 
		else {
			result = false;
		}
		return result;
	}
		
	/**
	 * Checks if given proxy is one of the players
	 *
	 * @param  proxy    Reference to view proxy object.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized boolean isInGame(ViewProxy proxy) throws IOException {
		return (proxy == player1 || proxy == player2);
	}
	
	/**
	 * Join a given session.
	 *
	 * @param  n  Player name
	 * @param  proxy    Reference to view proxy object.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join(ViewProxy proxy, String n) throws IOException {
	}
	
	/**
	 * Specify number of 
	 * markers taken from heap h.
	 *
	 * @param  h    Heap number
	 * @param  m  	Markers number
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void take(int h, int m) throws IOException {
		// Update stacks
		markers[h] -= m;
		// Report update to clients
		try {	
			player1.heap( h, markers[h] );
			player2.heap( h, markers[h] );
			
			// Check if game is won
			if (markers[0] == 0 && markers[1] == 0 && markers[2] == 0 ) {
				if (current_turn_id == id1 ) {
					score1++;
					player1.score(id1, score1);
					player2.score(id1, score1);
					player1.win( id1 );
					player2.win( id1 );
				} else {
					score2++;
					player1.score(id2, score2);
					player2.score(id2, score2);
					player1.win( id2 );
					player2.win( id2 );
				}
			} 
			
			// Switch current turn to other player
			if (current_turn_id == id1 ) {
				current_turn_id = id2;
			} else {
				current_turn_id = id1;
			}
			player1.turn( current_turn_id );
			player2.turn( current_turn_id );
			
		} catch (IOException exc) {
		}
	}
	
	/**
	 * Start a new game.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void newGame() throws IOException{
		markers[0] = 3;
		markers[1] = 4;
		markers[2] = 5;
		current_turn_id = id1;
		try {
			player1.heap(0, markers[0]);
			player2.heap(0, markers[0]);
			player1.heap(1, markers[1]);
			player2.heap(1, markers[1]);
			player1.heap(2, markers[2]);
			player2.heap(2, markers[2]);
			player1.turn( id1 );
			player2.turn( id1 );
		} catch (IOException exc) {
		}
	}
	
	/**
	 * Quit the game
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException {
		player1.quit();
		if (player2 != null) 
			player2.quit();
	}
	
	
	
}