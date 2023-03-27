import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message {
	public static final ArrayList<Message> sentMessages = new ArrayList<>();
	protected ChatClient from, to;
	protected String message;
	protected LocalDateTime date;

	public void save() {
		synchronized (sentMessages) {
			sentMessages.add(this);
		}
	}

	public static Message getMessage(ChatClient from, ChatClient to, String message, MessageType type) {
		return type == MessageType.Personal ? new PersonalMessage(from, to, message)
				: new BroadcastMessage(from, message);
	}

	public String toString() {
		return null;
	};
}
