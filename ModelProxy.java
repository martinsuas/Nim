import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Class ModelProxy provides the network proxy for the model object in the
 * Nim game. The model proxy resides in the client program and
 * communicates with the server program.
 *
 * @author  Martin Suarez
 * @version 11/02/2015
 */
public class ModelProxy implements ViewListener {
	
	// Hidden data members.
	
	private DatagramSocket mailbox;
	private SocketAddress destination;
	private ModelListener modelListener;
	
	// Exported constructors.
	
	/**
	 * Construct a new model proxy.
	 *
	 * @param  socket  Socket.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public ModelProxy 
		( DatagramSocket mailbox,
		  SocketAddress destination) throws IOException {
		this.mailbox = mailbox;
		this.destination = destination;
	}
	
	// Exported operations
	
	/**
	 * Set the model listener object for this model proxy.
	 *
	 * @param  modelListener  Model listener.
	 */
	public void setModelListener( ModelListener modelListener ) {
		this.modelListener = modelListener;
		new ReaderThread() . start();
	}
	
	/**
	 * Join a given session.
	 *
	 * @param  n  Player name
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void join(ViewProxy proxy, String n) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream ( baos );
		out.writeByte( 'J' );
		out.writeUTF( n );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket (payload, payload.length, destination));
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'T' );
		out.writeByte( h );
		out.writeByte( m );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket (payload, payload.length, destination));
	}
	
	/**
	 * Start a new game.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void newGame() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream( baos );
		out.writeByte( 'N' );
		out.close();
		byte[] payload = baos.toByteArray();
		mailbox.send(new DatagramPacket (payload, payload.length, destination));
	}
	
	/**
	 * Quit the game
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
		mailbox.send(new DatagramPacket (payload, payload.length, destination));
	}
	
	// Hidden heleper classes
	
	/**
	 * Class ReaderThread receives messages from the network, decodes them, and
	 * invokes the proper methods to process them.
	 */
	private class ReaderThread extends Thread {
		public void run() {
			byte[] payload = new byte[ 128 ];
			try {
				for( ;; ) {
					DatagramPacket packet =
						new DatagramPacket ( payload, payload.length );
					mailbox.receive (packet);
					DataInputStream in = 
						new DataInputStream
							( new ByteArrayInputStream
								(payload, 0, packet.getLength()));
					int id, s, h, m;
					String name;
					byte b = in.readByte();
					switch( b )
						{
						case 'I':
							id = in.readByte();
							modelListener.id( id );
							break;
						case 'A':
							id = in.readByte();
							name = in.readUTF();
							modelListener.name( id, name );
							break;
						case 'S':
							id = in.readByte();
							s = in.readByte();
							modelListener.score( id, s );
							break;
						case 'H':
							h = in.readByte();
							m = in.readByte();
							modelListener.heap( h, m );
							break;
						case 'U':
							id = in.readByte();
							modelListener.turn( id );
							break;
						case 'W':
							id = in.readByte();
							modelListener.win( id );
							break;
						case 'Q':
							modelListener.quit();
							break;
						default:
							System.err.println ("Bad message");
							break;
						}
				}
			} catch (IOException exc) { }
			finally {
				mailbox.close();
			}
		}
	}
}