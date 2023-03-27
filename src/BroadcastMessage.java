import java.time.LocalDateTime;

public final class BroadcastMessage extends Message {
	public BroadcastMessage(ChatClient from, String message) {
		this.from = from;
		this.message = message;
		date = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "---------------------------------------------\n" + "Broadcast: " + message + "\n"
				+ "Message sender: \n " + "\tAdress is " + from.getAddress() + "\tPort is " + "Date: " + date.toString()
				+ "\n---------------------------------------------\n";
	}
}
