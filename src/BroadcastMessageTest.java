import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class BroadcastMessageTest {
	String message;
	Socket socket;
	ServerSocket listener;
	ChatClient client;
	int port = 8080;
	int clientID = 0;

	@Test
	void test() {
		try {
			listener = new ServerSocket(port);
			socket = listener.accept();
			client = new ChatClient(socket, ++clientID);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		String message = "Hello, world!";
		BroadcastMessage broadcastMessage = new BroadcastMessage(client, message);
		String expectedOutput = "---------------------------------------------\n" + "Broadcast: " + message + "\n"
				+ "Message sender: \n " + "\tAdress is " + client.getAddress() + "\tPort is " + client.getPort()
				+ "\tDate: " + LocalDateTime.now() + "\n" + "---------------------------------------------\n";
		String actualOutput = broadcastMessage.toString();

		assertEquals(expectedOutput, actualOutput);
	}

}
