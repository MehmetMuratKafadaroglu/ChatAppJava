import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ChatServer {
	private int clientID = 0;
	private int port = 8080;
	private TreeMap<Integer, ChatClient> clients = new TreeMap<>();
	private static ChatServer server;

	private ChatServer() {
	}

	public static ChatServer getInstance() {
		return server;
	}

	public static void main(String[] args) {
		server = new ChatServer();
		server.run();
	}

	public Collection<ChatClient> getClients() {
		return clients.values();
	}

	public ChatClient getCoordinator() {
		Entry<Integer, ChatClient> entry = clients.firstEntry();
		return entry == null ? null : entry.getValue();
	}

	public boolean isClientRegistered(int clientID) {
		return clients.get(clientID) != null;
	}

	public void addClient(ChatClient client) {
		if (isClientRegistered(client.getClientID()))
			return;
		synchronized (clients) {
			clients.put(clientID, client);
			for (ChatClient oldClient : getClients())
				oldClient.sendToClient("New client joined\n" + client.describe());

		}

	}

	public void dropClient(ChatClient client) {
		synchronized (client) {
			clients.remove(client.getClientID());
		}
	}

	public void broadcastMessage(ChatClient from, String message) {
		for (ChatClient client : clients.values()) {
			client.sendToClient(from.getClientID() + ": " + message);
		}
		Message.getMessage(from, null, message, MessageType.Broadcast).save();
	}

	public boolean sendMessage(int messageTarget, String message, ChatClient from) {
		ChatClient to = clients.get(messageTarget);
		if (to == null)
			return false;
		to.sendToClient("{Private} " + from.getClientID() + ": " + message);
		Message.getMessage(from, to, message, MessageType.Personal).save();
		return true;
	}

	public void run() {
		try (ServerSocket listener = new ServerSocket(port)) {
			System.out.println(
					"Server is running on " + listener.getInetAddress().toString() + ":" + listener.getLocalPort());
			while (true) {
				Socket socket = listener.accept();
				clientID++;
				ChatClient newClient = new ChatClient(socket, clientID);
				addClient(newClient);
				newClient.start();
			}
		} catch (IOException e) {
			System.out.print("Server IO exception");
		} catch (Throwable e) {
			// Enters here if cannot initialize the ChatClient
			e.printStackTrace();
		}
	}

}
