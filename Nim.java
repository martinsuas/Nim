
import java.net.InetSocketAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.io.IOException;

/**
 * Class Nim is the client main program for the Nim network game. 
 * Usage: java Nim <I>serverhost</I> <I>serverport</I>
 * 				   <I>clienthost</I> <I>clientport</I> <I>playername</I>
 *
 * @author  Martin Suarez
 * @version 12/02/2015
 */
public class Nim {
	
	/**
	 * Main program.
	 */
	public static void main (String[] args) throws Exception {
		if (args.length != 5) usage();
		try {
			String serverhost = args[0];
			int serverport = Integer.parseInt( args[1] );
			String clienthost = args[2];
			int clientport = Integer.parseInt( args[3] );
			String playername = args[4];
			try {
				DatagramSocket mailbox =
					new DatagramSocket
						(new InetSocketAddress (clienthost, clientport));
				
				NimUI view = NimUI.create( playername );
				final ModelProxy proxy = 
					new ModelProxy( 
						mailbox,
						new InetSocketAddress(serverhost, serverport));
				view.setViewListener( proxy );
				proxy.setModelListener( view );
				
				Runtime.getRuntime().addShutdownHook ( new Thread() {
						public void run() {
							try { proxy.quit(); }
							catch (IOException exc ) {}
						}
				});
							
				proxy.join ( null, playername );
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
		System.err.println( "Usage: java Nim <clienthost> <clientport> " + 
			"<serverhost> <serverport> <playername>" );
		System.exit( 1 );
	}
}
