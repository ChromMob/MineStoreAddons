package me.chrommob.MineStoreAddons.socket;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.data.SendableKey;
import me.chrommob.MineStoreAddons.socket.data.WelcomeData;
import me.chrommob.minestore.common.MineStoreCommon;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;

public class ConnectionHandler extends WebSocketClient {
    private MineStoreAddonsMain main;
    private Gson gson = new Gson();
    private Set<SocketResponse> socketResponses = new HashSet<>();
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher decryptRSA;
    private Cipher encryptRSA;
    private PublicKey serverPublicKey;
    private SecretKey secretKey;
    private Cipher encryptAES;
    private Cipher decryptAES;
    private boolean welcomeSent;
    private MessageDigest digestSha;
    private KeyFactory keyFactoryRSA;

    public ConnectionHandler(String serverUri, MineStoreAddonsMain main) {
        super(URI.create(serverUri));
        welcomeSent = false;
        this.main = main;
        KeyPairGenerator kpg = null;
        KeyGenerator kg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kg = KeyGenerator.getInstance("AES");
            keyFactoryRSA = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(2048, new SecureRandom());
        KeyPair kp = kpg.generateKeyPair();
        privateKey = kp.getPrivate();
        publicKey = kp.getPublic();
        kg.init(256);
        secretKey = kg.generateKey();
        try {
            decryptRSA = Cipher.getInstance("RSA");
            encryptRSA = Cipher.getInstance("RSA");
            decryptAES = Cipher.getInstance("AES");
            encryptAES = Cipher.getInstance("AES");

            digestSha = MessageDigest.getInstance("SHA-256");

            decryptRSA.init(Cipher.DECRYPT_MODE, privateKey);
            decryptAES.init(Cipher.DECRYPT_MODE, secretKey);
            encryptAES.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        main.reconnected();
        MineStoreCommon.getInstance().log("Connected to websocket");
    }

    private void onPublicKey(String message) {
        message = message.replace("publickey-", "");
        SendableKey key = gson.fromJson(message, SendableKey.class);
        try {
            serverPublicKey = keyFactoryRSA.generatePublic(new X509EncodedKeySpec(key.getEncoded()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        try {
            encryptRSA.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        send(gson.toJson(new WelcomeData(new SendableKey(publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getEncoded()), new SendableKey(secretKey.getAlgorithm(), secretKey.getFormat(), secretKey.getEncoded()), encryptRSA, encryptAES)));
        if (main.messages.size() > 0) {
            main.messages.forEach(this::send);
            main.messages.clear();
        }
    }

    @Override
    public void send(String text) {
        if (isClosed()) {
            main.messages.add(text);
            main.connectToWebsocket();
            return;
        }
        if (!welcomeSent && gson.fromJson(text, WelcomeData.class) != null) {
            super.send(text);
            welcomeSent = true;
            return;
        }
        if (serverPublicKey == null) {
            main.messages.add(text);
            return;
        }
        try {
            byte[] array = encryptAES.doFinal(text.getBytes());
            byte[] hash = encryptRSA.doFinal(digestSha.digest(array));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(array);
            outputStream.write(hash);
            super.send(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        if (message.startsWith("publickey")) {
            onPublicKey(message);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        byte[] array = bytes.array();
        String message = null;
        try {
            byte[] hash = new byte[256];
            System.arraycopy(array, array.length - 256, hash, 0, 256);
            hash = decryptRSA.doFinal(hash);
            byte[] messageBytes = new byte[array.length - 256];
            System.arraycopy(array, 0, messageBytes, 0, array.length - 256);
            byte[] ourHash = digestSha.digest(messageBytes);
            int intHash = ByteBuffer.wrap(hash).getInt();
            int intOurHash = ByteBuffer.wrap(ourHash).getInt();
            boolean equals = intHash == intOurHash;
            if (!equals) {
                MineStoreCommon.getInstance().log("Hashes don't match, closing connection");
                close();
                return;
            }
            message = new String(decryptAES.doFinal(messageBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (SocketResponse socketResponse : socketResponses) {
            socketResponse.onResponse(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        MineStoreCommon.getInstance().log("Disconnected from websocket");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void addMessage(String message) {
        send(message);
    }

    public void registerSocketResponse(SocketResponse socketResponse) {
        socketResponses.add(socketResponse);
    }
}
