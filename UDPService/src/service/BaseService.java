package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.naming.directory.InvalidAttributesException;

/**
 * Base implementation of a service.
 */
public class BaseService implements Service, Runnable {
	
	protected int maxPacketSize = 64000;
	protected DatagramSocket socket;
	protected InetAddress address;
	protected int port;
	protected boolean running = true;
	protected ServiceListener listener = null;
	protected boolean multicast = false;
	
	/**
	 * Create a base service for receive and send messages using UDP.
	 * @param address Default address to send message.
	 * @param port Port to listen.
	 * @param multicast If this is a multicast service. 
	 * The group that this service will join is specified by the address passed to this contructor.
	 * @param listener Listener that will receive the callback with the received message.
	 * @throws IOException When it is not possible to create a socket.
	 * 
	 * @see Note that this don't initiate the socket that will be use to send. So is your responsibity to do so.
	 */
	public BaseService(InetAddress address, int port, boolean multicast, ServiceListener listener) throws IOException {
		this.listener = listener;
		this.address = address;
		this.port = port;
		this.multicast = multicast;
		try {
			if (multicast) {
				this.socket = new MulticastSocket(port);
				((MulticastSocket) this.socket).joinGroup(address);
			}
			else {
				this.socket = new DatagramSocket(port);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		new Thread(this).start();
	}
	
	/**
	 * Create a UDP service.
	 * @param address Default address to send messages.
	 * @param port Port to listen.
	 * @param listener Listener that will receive callback.
	 * @throws IOException
	 */
	public BaseService(InetAddress address, int port, ServiceListener listener) throws IOException {
		this(address, port, false, listener);
	}
	
	/**
	 * Create a UDP service.
	 * @param port Port to listen.
	 * @param listener Listener that will receive callback.
	 * @throws IOException
	 */
	public BaseService(int port, ServiceListener listener) throws IOException {
		this(null, port, false, listener);
	}
	
	@Override
	public void send(Message<?> message, InetAddress address, int port) throws IOException {
		try {
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, port);
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Send a message to the default address and port. If it there's none, throw argument invalid exception.
	 * @param message Message to send.
	 * @throws IOException 
	 * @throws InvalidAttributesException 
	 */
	@Override
	public void send(Message<?> message) throws IOException, InvalidAttributesException {
		if (this.address == null) {
			throw new InvalidAttributesException("Default addres is null!");
		}
		this.send(message, this.address, this.port);
	}
	
	@Override
	public void run() {
		while (running) {
			byte[] content = new byte[this.maxPacketSize];
			DatagramPacket packet = new DatagramPacket(content, content.length);
			try {
				socket.receive(packet);
				Message<?> message = Message.decode(packet.getData());
				this.listener.receive(packet, message);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop this service. After this call, it will not be useful anymore.
	 */
	public void stop() {
		this.running = false;
	}
	
	/**
	 * @return The max size of a UDP Packet to send and receive.
	 */
	public int getMaxPacketSize() {
		return this.maxPacketSize;
	}
	
	/**
	 * @param max The max size of a UDP Packet to send and receive.
	 */
	public void setMaxPacketSize(int max) {
		this.maxPacketSize = max;
	}
}
