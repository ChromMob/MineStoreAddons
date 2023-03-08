package me.chrommob.MineStoreAddons.socket.data;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.config.ConfigKey;

public class WelcomeData {
    private String storeUrl = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.STORE_URL);
    private boolean apiEnabled = (boolean) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_ENABLED);
    private String apiKey = (String) MineStoreCommon.getInstance().configReader().get(ConfigKey.API_KEY);
}
