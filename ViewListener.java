import java.io.IOException;

/**
 * Interface ViewListener specifies the interface for an object that is
 * triggered by events from the view object in the Nim game.
 *
 * @author  Martin Suarez
 * @version 11/02/2015
 */
public interface ViewListener {
	
	/**
	 * Join a given session.
	 *
	 * @param  n  Player name
	 * @param  proxy    Reference to view proxy object.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join(ViewProxy proxy, String n) throws IOException;
	
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
	public void take(int h, int m) throws IOException;
	
	/**
	 * Start a new game.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void newGame() throws IOException;
	
	/**
	 * Quit the game
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException;
	
}