package im.yesido.websocket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppTest {

	static List<MsgWebSocketClient> clients = new ArrayList<MsgWebSocketClient>();
	static String serverUri = "ws://localhost:8001/websocket?account={1}&password=123456";
	public static void main(String[] args) throws URISyntaxException {
		int i = 1;
		while(i < 10000) {
			String account = UUID.randomUUID().toString();
			String uri = serverUri;
			uri = uri.replace("{1}", account);
			MsgWebSocketClient client = new MsgWebSocketClient(uri, i);
			client.connect();
			clients.add(client);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
