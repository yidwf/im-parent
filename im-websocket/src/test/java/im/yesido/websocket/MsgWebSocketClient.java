package im.yesido.websocket;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class MsgWebSocketClient extends WebSocketClient {

	private String serverUri;
	private int index;
	public MsgWebSocketClient(String serverUri, int index) throws URISyntaxException {
		super(new URI(serverUri));
		this.serverUri = serverUri;
		this.index = index;
	}

	@Override
	public void onOpen(ServerHandshake shake) {
		System.out.println("index-->"+index+"握手成功:" + serverUri);
	}

	@Override
	public void onMessage(String paramString) {
		System.out.println("接收到消息：" + paramString);

	}

	@Override
	public void onClose(int paramInt, String paramString, boolean paramBoolean) {
		System.out.println("index-->"+index+"关闭:" + serverUri);
	}

	@Override
	public void onError(Exception e) {
		System.out.println("index-->"+index+"异常:" + serverUri + e);

	}

}
