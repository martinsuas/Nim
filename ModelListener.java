import java.io.IOException;

/**
 * Interface ModelListener specifies the interface for an object that is
 * triggered by events from the model object in the Nim game.
 *
 * @author  Martin Suarez
 * @version 11/02/2015
 */
public interface ModelListener  {
	
	/**
	 * Report the player's id
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void id( int i ) throws IOException;
	
	/**
	 * Report the identity of a player
	 * @param  i      Player id
	 * @param  n      Player name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void name(int i, String n) throws IOException;
	
	/**
	 * Report the score of a player
	 * @param  i      Player id
	 * @param  s      Score
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void score(int i, int s) throws IOException;
	
	/**
	 * Report the state of a heap ( the number of markers it has
	 * left.
	 * @param  h      Heap id
	 * @param  m      Number of markers
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void heap(int h, int m) throws IOException;
	
	/**
	 * Report who's turn it is
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void turn(int i) throws IOException;
	
	/**
	 * Report a player has won
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void win(int i) throws IOException;
	
	/**
	 * Report the game has ended.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException;
	
}