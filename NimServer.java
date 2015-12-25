
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;

/**
 * NimServer is the server main program for the Nim network game. 
 * Usage: java NimServer <I>host</I> <I>port</I>
 *
 * @author  Martin Suarez
 * @version 12/02/2015
 */
public class NimServer {
	
	/**
	 * Main program.
	 */
	public static void main (String[] args) throws Exception {
		if (args.length != 2) usage();
		try {
			String host = args[0];
			int port = Integer.parseInt( args[1] );
			
			try {
				DatagramSocket mailbox =
					new DatagramSocket
						(new InetSocketAddress (host, port));
				
				MailboxManager manager = new MailboxManager( mailbox );
				
				for (;;) {
					manager.receiveMessage();
				}
			}
			catch (SocketException e) {
				System.err.println( "SocketException: " + e.getMessage());
				System.exit(1);
			}
			
		} 
		catch (NumberFormatException e) {
			System.err.println( "NumberFormatException: " + e.getMessage()
				+ " must be a valid port number.");
			System.exit(1);
		}
	}
	
	/**
	 * Print a usage message and exit.
	 */
	private static void usage() {
		System.err.println( "Usage: java NimServer <host> <port>");
		System.exit( 1 );
	}
}
