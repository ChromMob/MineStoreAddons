package me.chrommob.MineStoreAddons.socket.data;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.config.ConfigKey;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;


public class WelcomeData {
    private SendableKey publicKey;
    private byte[] storeUrl;
    private boolean apiEnabled;
    private byte[] apiKey;
    private byte[] AES;
    public WelcomeData(SendableKey publicKey, SendableKey AES, Cipher encryptRsa, Cipher encryptAES) {
        this.publicKey = publicKey;
        String storeUrl = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.STORE_URL);
        apiEnabled = (boolean) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_ENABLED);
        String apiKey = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_KEY);
        try {
            this.storeUrl = encryptAES.doFinal(storeUrl.getBytes(StandardCharsets.UTF_8));
            this.apiKey = encryptAES.doFinal(apiKey.getBytes(StandardCharsets.UTF_8));
            this.AES = encryptRsa.doFinal(AES.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
