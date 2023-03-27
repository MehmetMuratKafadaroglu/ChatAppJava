import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class ChatClient extends Thread {
	private PrintWriter out;
	private BufferedReader in;
	private LinkedList<String> sendQueue = new LinkedList<String>();
	private ChatServer server;
	private Socket socket;
	private int clientID;
	private boolean isActive = true;
	private boolean run = true;
	private int waitCount = 0;
	private int port;
	private String address;
	private String userName;

	private final static int waitingTime = 10;// In minutes

	public ChatClient(Socket socket, int clientID) throws Throwable {
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.server = ChatServer.getInstance();
		this.socket = socket;
		this.clientID = clientID;
		this.port = socket.getPort();// Default port
		this.address = socket.getInetAddress().toString();
		this.userName = "Guest " + clientID;
	}

	@Override
	public void run() {
		out.println("Welcome to the chat");
		while (socket.isConnected() && run) {
			try {
				if (isClientWriting()) {
					setClientIsActive();
					handleCommands();
				} else {
					printMessageFromQue();
					waitCount++;
					Thread.sleep(100);// Sleep for 0.1 seconds
					updateClientActivity();

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public String getUsername() {
		return this.userName;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public static int getWaitingtime() {
		return waitingTime;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getClientID() {
		return clientID;
	}

	public void setClientID(int clientID) {
		this.clientID = clientID;
	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
		return address;
	}

	public String describe() {
		return (isActive ? "Active" : "Inactive") + " " + "\nAdress: " + this.address + " Port: " + this.port + " ID: "
				+ this.clientID + "\nUser name: " + this.userName;
	}

	public void sendToClient(String message) {
		synchronized (sendQueue) {
			sendQueue.add(message);
		}
	}

	public void setUsername(String userName) {
		this.userName = userName;
	}

	private boolean isClientWriting() throws IOException {
		return socket.getInputStream().available() > 0;
	}

	private void printMessageFromQue() {
		String toSend = null;
		synchronized (sendQueue) {
			if (sendQueue.size() > 0)
				toSend = sendQueue.remove(0);
		}
		if (toSend != null)
			out.println(toSend);
	}

	private boolean amICoordinator() {
		return server.getCoordinator() == this;
	}

	private void updateClientActivity() throws IOException {
		if (waitCount >= ChatClient.waitingTime * 10 * 60) {
			waitCount = 0;
			isActive = false;
			if (amICoordinator())
				quit();

		}
	}

	private void sendMessage(String[] messages) {
		if (messages.length == 1) {
			missingArguments();
			return;
		}
		int messageTarget;
		try {
			messageTarget = Integer.parseInt(messages[0]);
		} catch (NumberFormatException e) {
			noTargetValue();
			return;
		}
		boolean isTargetExists = server.sendMessage(messageTarget, messages[1], this);
		if (!isTargetExists) {
			targetDoesntExist();
			return;
		}
		sendToClient("Message send succesfully");
	}

	private void broadcastMessage(String message) {
		server.broadcastMessage(this, message);
		sendToClient("Broadcast made");
	}

	private void setPort(String port) {
		try {
			this.port = Integer.parseInt(port);
		} catch (Throwable t) {
			improperArgument();
		}
		sendToClient("Your port is updated to: " + port);
	}

	public void setAddress(String address) {
		for (char c : address.toCharArray()) {
			if (c == ' ')
				improperArgument();
		}
		this.address = address;
		sendToClient("Your adress is updated to: " + address);
	}

	private void printCoordinator() {
		out.println(server.getCoordinator().describe());
	}

	private void printClients() {
		for (ChatClient client : server.getClients()) {
			out.println(client.describe());
		}
	}

	private void history() {
		for (Message message : Message.sentMessages) {
			sendToClient(message.toString());
		}
	}

	private void setClientIsActive() {
		waitCount = 0;
		isActive = true;
	}

	private void inform() {
		sendToClient("You are the new Coordinator");
	}

	private void quit() throws IOException {
		if (amICoordinator()) {
			server.dropClient(this);
			ChatClient newCoordinator = server.getCoordinator();
			if (newCoordinator != null)
				newCoordinator.inform();
		} else {
			server.dropClient(this);
		}
		run = false;
		socket.close();
	}

	private void missingArguments() {
		out.println("You have passed missing arguments");
	}

	private void targetDoesntExist() {
		out.println("Message target you are trying to send doesn't exist");
	}

	private void improperArgument() {
		out.println("Improper argument");
	}

	private void noTargetValue() {
		out.println("Please provide a target for your message");
	}

	private void handleCommands() throws IOException {
		String[] commands = in.readLine().strip().split(" ", 2);
		switch (commands[0]) {
		case "broadcast":
			broadcastMessage(commands[1]);
			break;
		case "set-port":
			setPort(commands[1]);
			break;
		case "set-address":
			setAddress(commands[1]);
			break;
		case "set-username":
			setUsername(commands[1]);
			break;
		case "clients":
			printClients();
			break;
		case "history":
			history();
			break;
		case "quit":
			quit();
			break;
		case "coordinator":
			printCoordinator();
			break;
		default:
			sendMessage(commands);
		}
	}

}
