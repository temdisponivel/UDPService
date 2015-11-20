package service;

import java.io.IOException;

/**
 * Interface that defines a service.
 * @author temdi
 *
 */
public interface Service {
	/**
	 * Sends a message.
	 * @param message Message to sent.
	 * @throws IOException When it is not possible to send a message.
	 */
	void send(Message<?> message) throws IOException;
	void stop();
}
