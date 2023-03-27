import java.time.LocalDateTime;

public final class PersonalMessage extends Message {
	public PersonalMessage(ChatClient from, ChatClient to, String message) {
		this.from = from;
		this.to = to;
		this.message = message;
		date = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "---------------------------------------------\n" + "Message: " + message + "\n" + "Message sender: \n "
				+ "\tAdress is " + from.getAddress() + "\tPort is " + from.getPort() + "\nMessage reciever: \n"
				+ "\tAdress is " + to.getAddress() + "\tPort is " + to.getPort() + " Date: " + date.toString()
				+ "\n---------------------------------------------\n";
	}
}
