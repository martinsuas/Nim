import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Provides network proxy for the view object of the Nim game. It resides
 * in the server program and communicates with the client.
 * @author Martin Suarez
 * @version 12/02/2015
 */
public class ViewProxy implements ModelListener {
	
	// Hidden data members
	private DatagramSocket mailbox;
	private SocketAddress clientAddress;
	private ViewListener viewListener;
	
	// Exported constructors
	/**
	 * Construct a new view proxy.
	 * @param mailbox			Server's mailbox.
	 * @param clientAddress		Client's mailbox address.
	 */
	public ViewProxy (DatagramSocket mailbox, SocketAddress clientAddress) {
		this.mailbox = mailbox;
		this.clientAddress = clientAddress;
	}
	
	// Exported operations.
	/**
	 * Set the view listener object for this view proxy.
	 * @param  viewListener  	View listener.
	 */
	public void setViewListener( ViewListener viewListener ) {
		this.viewListener = viewListener;
	}
	
	/**
	 * Report the player's id
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void id( int i ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'I' );
		out.writeByte( i );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Report the identity of a player
	 * @param  i      Player id
	 * @param  n      Player name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void name(int i, String n) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'A' );
		out.writeByte( i );
		out.writeUTF( n );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Report the score of a player
	 * @param  i      Player id
	 * @param  s      Score
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void score(int i, int s) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'S' );
		out.writeByte( i );
		out.writeByte( s );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
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
	public void heap(int h, int m) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'H' );
		out.writeByte( h );
		out.writeByte( m );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Report who's turn it is
	 * @param  i      Player id
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void turn(int i) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'U' );
		out.writeByte( i );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Report a player has won
	 * @param  i      Player id
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void win(int i) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'W' );
		out.writeByte( i );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Report the game has ended.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void quit() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'Q' );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket(payload, payload.length, clientAddress));
	}
	
	/**
	 * Process a received datagram.
	 * @param 	datagram 		Datagram.
	 * @return	True to discard this view proxy, false otherwise
	 * @exception IOException
	 *		Thrown if an I/O error occurred.
	 **/
	 public boolean process (DatagramPacket datagram) throws IOException {
	 	boolean discard = false;
	 	DataInputStream in =
			new DataInputStream
				(new ByteArrayInputStream
					(datagram.getData(), 0, datagram.getLength()));
		int h, m;
		String name;
		byte b = in.readByte();
		switch(b)
			{
			case 'J':
				name = in.readUTF();
				viewListener.join( ViewProxy.this, name );
				break;
			case 'T':
				h = in.readByte();
				m = in.readByte();
				viewListener.take( h, m );
				break;
			case 'N':
				viewListener.newGame();
				break;
			case 'Q':
				discard = true;
				break;
			default:
				System.err.println( "Bad message" );
				break;
			}
		return discard;
	 }
	
}