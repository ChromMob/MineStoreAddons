package me.chrommob.MineStoreAddons.socket.data;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.config.ConfigKey;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;


public class WelcomeData {
    private SendableKey publicKey;
    private String storeUrl;
    private boolean apiEnabled;
    private byte[] apiKey;
    public WelcomeData(SendableKey publicKey, Cipher encrypt) {
        this.publicKey = publicKey;
        storeUrl = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.STORE_URL);
        apiEnabled = (boolean) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_ENABLED);
        String apiKey = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_KEY);
        try {
            this.apiKey = encrypt.doFinal(apiKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
