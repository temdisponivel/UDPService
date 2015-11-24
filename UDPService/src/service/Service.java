package service;

import java.io.IOException;
import java.net.InetAddress;

import javax.naming.directory.InvalidAttributesException;

/**
 * Interface that defines a service.
 * @author temdi
 *
 */
public interface Service {
	/**
	 * Sends a message.
	 * @param message Message to sent.
	 * @param address Address to send the message.
	 * @param port Port to send the message.
	 * @throws IOException When it is not possible to send a message.
	 */
	void send(Message<?> message, InetAddress address, int port) throws IOException;
	/**
	 * Sends a message to a default address and port defined in the class that implements this interface.
	 * @throws IOException When it is not possible to send a message.
	 */
	void send(Message<?> message) throws IOException, InvalidAttributesException;
	void stop();
}
