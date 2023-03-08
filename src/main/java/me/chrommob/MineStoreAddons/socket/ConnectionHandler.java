package me.chrommob.MineStoreAddons.socket;

import me.chrommob.minestore.common.MineStoreCommon;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectionHandler {
    private Client client;

    public ConnectionHandler() {
        new Thread(runnable).start();
    }

    private Set<String> toSend = new CopyOnWriteArraySet<>();
    private Set<SocketResponse> socketResponses = new HashSet<>();
    public void addMessage(String message) {
        if (client == null) {
            toSend.add(message);
            return;
        }
        client.send(message);
        if (toSend.isEmpty()) {
            return;
        }
        for (String s : toSend) {
            client.send(s);
        }
        toSend.clear();
    }

    private Runnable runnable = () -> {
        try {
            client = new Client(new URI("wss://ws.chrommob.fun"), this);
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void registerSocketResponse(SocketResponse socketResponse) {
        socketResponses.add(socketResponse);
    }
    public Set<SocketResponse> getSocketResponses() {
        return socketResponses;
    }
}

class Client extends WebSocketClient {
    ConnectionHandler connectionHandler;

    public Client(URI serverURI, ConnectionHandler connectionHandler) {
        super(serverURI);
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        MineStoreCommon.getInstance().log("You are connected to secure websocket server: " + getURI());
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        connectionHandler.getSocketResponses().forEach(socketResponse -> socketResponse.onResponse(message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        MineStoreCommon.getInstance().log(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        MineStoreCommon.getInstance().debug(ex);
        // if the error is fatal then onClose will be called additionally
    }
}

