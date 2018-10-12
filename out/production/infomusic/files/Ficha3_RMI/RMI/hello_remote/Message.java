import java.io.*;

public class Message implements Serializable {
	public String text;

	public Message(String text) {
		this.text = text;
	}

	public void change_text(String text) {
		this.text = text;
	}

	public String toString() {
		return text;
	}

}