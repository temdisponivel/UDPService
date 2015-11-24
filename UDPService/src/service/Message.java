package service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class Message<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected byte[] data;
	protected String messageData;
	
	/** 
	 * @param content Content of the message.
	 * @throws IOException If it can't serialize the object 
	 */
	public Message(T content) throws IOException {
		ByteArrayOutputStream outStream = null;
		ObjectOutputStream objOutStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			objOutStream = new ObjectOutputStream(outStream);
			objOutStream.writeObject(content);
			messageData = Base64.getEncoder().encodeToString(outStream.toByteArray());
			outStream.close();
			objOutStream.close();
			outStream = new ByteArrayOutputStream();
			objOutStream = new ObjectOutputStream(outStream);
			objOutStream.writeObject(this);
			data = outStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			objOutStream.close();
			outStream.close();
		}
	}
	
	/**
	 * @return The coded content of this message.
	 */
	public byte[] getBytes() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * @return The content of this message.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public T getData() throws IOException, ClassNotFoundException {
		ByteArrayInputStream inStream = null;
		ObjectInputStream objInStream = null;
		try {
			inStream = new ByteArrayInputStream(Base64.getDecoder().decode(this.messageData));
			objInStream = new ObjectInputStream(inStream);
			T message = (T) objInStream.readObject();
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			inStream.close();
			objInStream.close();
		}
	}
	
	/**
	 * Use this method to decode a message from a given byte array received from a DatagramPacket.
	 * @param content Content to decode into a message.
	 * @return Message<?>
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	static public Message<?> decode(byte[] content) throws IOException, ClassNotFoundException {
		ByteArrayInputStream inStream = null;
		ObjectInputStream objInStream = null;
		try {
			inStream = new ByteArrayInputStream(content);
			objInStream = new ObjectInputStream(inStream);
			Message<?> message = (Message<?>) objInStream.readObject();
			return message;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			inStream.close();
			objInStream.close();
		}
	}
}
