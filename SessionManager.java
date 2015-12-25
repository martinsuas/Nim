import java.io.IOException;
import java.util.ArrayList;
/**
 * Mantains the sessions' model objects.
 *
 * @author  Martin Suarez
 * @version 12/02/2015
 */
public class SessionManager implements ViewListener{
	
	// Hidden data members.
	private ArrayList<NimModel> sessions = 
		new ArrayList<NimModel>();
	
	// Exported constructors.
	/**
	 * Construct a new session manager.
	 */
	public SessionManager(){}
	
	/**
	 * Join a given session.
	 * @param  proxy    Reference to view proxy object.
	 * @param  n  Player name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join(ViewProxy proxy, String n) throws IOException {
		boolean space_found = false; 
		for( NimModel model : sessions ) {
			if (model.needsPlayer()) {
				space_found = true;
				proxy.setViewListener( model );
				model.addPlayer(  proxy, false, n );
			}
		}
		if (!space_found) {
			NimModel model = new NimModel();
			proxy.setViewListener( model );
			model.addPlayer( proxy, true, n );
			sessions.add( model );
		}
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
	public void take(int h, int m) throws IOException {
	}
	
	/**
	 * Start a new game.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void newGame() throws IOException {
	}
	
	/**
	 * Prepare to quit the game
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void prepareQuit(ViewProxy vp) throws IOException {
		for( int i = 0; i < sessions.size(); i++ ) {
			if (vp != null && sessions.get(i).isInGame(vp)) {
				sessions.get(i).quit();
				sessions.remove( i );
			}
		}
	}
	
	/**
	 * Quit the game
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException {
	}
	
}