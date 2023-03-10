package me.chrommob.MineStoreAddons.socket;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.data.WelcomeData;
import me.chrommob.minestore.common.MineStoreCommon;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class ConnectionHandler extends WebSocketClient {
    private MineStoreAddonsMain main;
    private Gson gson = new Gson();
    private Set<SocketResponse> socketResponses = new HashSet<>();

    public ConnectionHandler(String serverUri, MineStoreAddonsMain main) {
        super(URI.create(serverUri));
        this.main = main;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        MineStoreCommon.getInstance().log("Connected to websocket");
        send(gson.toJson(new WelcomeData()));
    }

    @Override
    public void onMessage(String message) {
        for (SocketResponse socketResponse : socketResponses) {
            socketResponse.onResponse(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        MineStoreCommon.getInstance().log("Disconnected from websocket");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                connect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onError(Exception ex) {
        MineStoreCommon.getInstance().log("Error in websocket");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addMessage(String message) {
        send(message);
    }

    public void registerSocketResponse(SocketResponse socketResponse) {
        socketResponses.add(socketResponse);
    }
}
