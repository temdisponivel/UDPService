package service;

import java.net.DatagramPacket;

public interface ServiceListener {
	/**
	 * Receive a message.
	 * @param packet Package received.
	 * @param receive Message receive within the packet.
	 */
	void receive(DatagramPacket packet, Message<?> receive);
}
