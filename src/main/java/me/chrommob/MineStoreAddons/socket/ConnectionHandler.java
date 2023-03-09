package me.chrommob.MineStoreAddons.socket;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.socket.data.WelcomeData;

import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConnectionHandler {
    private Gson gson = new Gson();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ConnectionHandler() {
        new Thread(runnable).start();
    }

    private Set<String> toSend = new CopyOnWriteArraySet<>();
    private Set<SocketResponse> socketResponses = new HashSet<>();
    public void addMessage(String message) {
        toSend.add(message);
    }
    public void removeMessage(String message) {
        toSend.remove(message);
    }

    private Runnable runnable = () -> {
        try {
            socket = new Socket("ws.chrommob.fun", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            out.println(gson.toJson(new WelcomeData()));
            while (true) {
                for (String message : toSend) {
                    out.println(message);
                    removeMessage(message);
                }
                while (in.ready()) {
                    String message = in.readLine();
                    for (SocketResponse socketResponse : socketResponses) {
                        socketResponse.onResponse(message);
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void registerSocketResponse(SocketResponse socketResponse) {
        socketResponses.add(socketResponse);
    }
}
