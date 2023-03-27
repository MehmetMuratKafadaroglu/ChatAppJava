import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

//IMPORTANT NOTE
//Before you run this test make sure to start the server!!

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatClientTest {
	private static Socket firstSocket, secondSocket;
	private static PrintWriter firstOut, secondOut;
	private static BufferedReader firstIn, secondIn;
	private int firstID, secondID;

	@Test
	@Order(1)
	public void testRegister() throws IOException {
		try {
			firstSocket = new Socket(InetAddress.getLocalHost(), 8080);
			firstOut = new PrintWriter(firstSocket.getOutputStream(), true);
			firstIn = new BufferedReader(new InputStreamReader(firstSocket.getInputStream()));

			secondSocket = new Socket(InetAddress.getLocalHost(), 8080);
			secondOut = new PrintWriter(secondSocket.getOutputStream(), true);
			secondIn = new BufferedReader(new InputStreamReader(secondSocket.getInputStream()));
			firstIn.readLine();
			secondIn.readLine();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		firstOut.println("register 1 1");
		secondOut.println("register 2 2");

		String response = firstIn.readLine();
		String tokens[] = response.split(" ");
		firstID = Integer.parseInt(tokens[6]);

		tokens = secondIn.readLine().split(" ");
		secondID = Integer.parseInt(tokens[6]);

		assertEquals("Registration Successful", tokens[0] + " " + tokens[1]);
		System.out.println("First client id: " + firstID + "\n" + "Second client id: " + secondID);
	}

	@Test
	@Order(2)
	public void testSendMessage() throws IOException {
		String message = "Hello, how are you doing?";
		firstOut.println("send-message " + 2 + " " + message);
		System.out.println(secondIn.readLine());
		System.out.println(secondIn.readLine());
		System.out.println(secondIn.readLine());

		System.out.println(firstIn.readLine());
		System.out.println(firstIn.readLine());

		System.out.println(firstIn.readLine());
		secondIn.readLine();
		String responses = secondIn.readLine();

		System.out.println(responses);
		assertEquals(true, responses.equals(message));
	}

}
