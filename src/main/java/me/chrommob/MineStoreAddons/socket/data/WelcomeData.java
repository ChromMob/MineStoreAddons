package me.chrommob.MineStoreAddons.socket.data;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.config.ConfigKey;

import java.security.PublicKey;

public class WelcomeData {
    private SendableKey publicKey;
    private String storeUrl;
    private boolean apiEnabled;
    private String apiKey;
    public WelcomeData(SendableKey publicKey) {
        this.publicKey = publicKey;
        storeUrl = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.STORE_URL);
        apiEnabled = (boolean) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_ENABLED);
        apiKey = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_KEY);
    }
}
