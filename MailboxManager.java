import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;

public class MailboxManager {
	// Hidden data members.
	private DatagramSocket mailbox;
	private HashMap<SocketAddress,ViewProxy> proxyMap =
		new HashMap<SocketAddress,ViewProxy>();

	private byte[] payload = new byte [128];
	private SessionManager sessionManager = new SessionManager();
	
	// Exported constructors.
	/**
	 * Construct a new mailbox manager.
	 *
	 * @param  mailbox  Mailbox used to read datagrams.
	 */
	public MailboxManager (DatagramSocket mailbox) {
		this.mailbox = mailbox;
	}

	// Exported operations.
	/**
	 * Receive and process the next datagram.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurrs
	 */
	public void receiveMessage() throws IOException {
		DatagramPacket packet = new DatagramPacket (payload, payload.length);
		mailbox.receive (packet);
		SocketAddress clientAddress = packet.getSocketAddress();
		ViewProxy proxy = proxyMap.get (clientAddress);
		if (proxy == null) {
			proxy = new ViewProxy (mailbox, clientAddress);
			proxy.setViewListener (sessionManager);
			proxyMap.put (clientAddress, proxy);
		}
		if (proxy.process (packet)) {
			sessionManager.prepareQuit(proxy);
			proxyMap.remove (clientAddress);
		}
	}
}