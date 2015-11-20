package service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Base implementation of a service.
 */
public class BaseService implements Service, Runnable {

	protected MulticastSocket socket;
	protected InetAddress address;
	protected int port;
	protected boolean running = true;
	protected ServiceListener listener = null;
	
	/**
	 * Create a base service for receive and send messages using UDP.
	 * @param address Address to send message.
	 * @param port Port to bind..
	 * @param listener Listener that will receive the callback with the received message.
	 * @throws IOException When it is not possible to create a socket.
	 * 
	 * @see Note that this don't initiate the socket that will be use to send. So is your responsibity to do so.
	 */
	public BaseService(InetAddress address, int port, ServiceListener listener) throws IOException {
		this.listener = listener;
		this.address = address;
		this.port = port;
		try {
			this.socket = new MulticastSocket(port);
			this.socket.joinGroup(address);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		new Thread(this).start();
	}
	
	@Override
	public void send(Message<?> message) throws IOException {
		try {
			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, this.address, this.port);
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void run() {
		while (running) {
			byte[] content = new byte[4096];
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
}
