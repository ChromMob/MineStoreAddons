package me.chrommob.MineStoreAddons;

import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.config.ConfigHandler;
import me.chrommob.MineStoreAddons.features.announcer.Announcer;
import me.chrommob.MineStoreAddons.features.economy.CustomEconomy;
import me.chrommob.MineStoreAddons.features.userInfo.UserInfo;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreAddon;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class MineStoreAddonsMain extends MineStoreAddon {
    private MineStoreCommon common;
    private ConnectionHandler connectionHandler;
    private File configFile;
    private ConfigHandler configHandler;
    private Yaml yaml;
    private Announcer announcer;
    private UserInfo userInfo;
    private CustomEconomy customEconomy;

    @Override
    public void onLoad() {
        common = MineStoreCommon.getInstance();
        configFile = new File(MineStoreCommon.getInstance().configFile().getParentFile() + File.separator + "addons" + File.separator + "MineStoreAddons" + File.separator + "config.yml");
        configHandler = new ConfigHandler(configFile);
        if ((boolean) configHandler.get(ConfigAddonKeys.ECONOMY_ENABLED)) {
            customEconomy = new CustomEconomy(this);
            common.registerPlayerEconomyProvider(customEconomy);
        }
    }

    @Override
    public void onEnable() {
        if (customEconomy != null) {
            customEconomy.onEnable();
        }
        registerListeners();
        connectToWebsocket();
    }

    private boolean reconnecting = false;
    public void connectToWebsocket() {
        if (reconnecting) return;
        connectionHandler = new ConnectionHandler("ws://ws.chrommob.fun:8080", this);
        connectionHandler.connect();
        registerSocketResponses();
    }

    public void reconnected() {
        reconnecting = false;
    }
    public Set<String> messages = new HashSet<>();

    private void registerSocketResponses() {
        if ((boolean) configHandler.get(ConfigAddonKeys.PURCHASE_ANNOUNCER_ENABLED)) {
            connectionHandler.registerSocketResponse(this.announcer);
        }
        if ((boolean) configHandler.get(ConfigAddonKeys.USER_INFO_ENABLED)) {
            connectionHandler.registerSocketResponse(this.userInfo);
        }
        if ((boolean) configHandler.get(ConfigAddonKeys.ECONOMY_ENABLED)) {
            connectionHandler.registerSocketResponse(customEconomy);
        }
    }

    private void registerListeners() {
        if ((boolean) configHandler.get(ConfigAddonKeys.PURCHASE_ANNOUNCER_ENABLED)) {
            this.announcer = new Announcer(this);
            common.registerListener(new Announcer(this));
        }
        if ((boolean) configHandler.get(ConfigAddonKeys.USER_INFO_ENABLED)) {
            this.userInfo = new UserInfo(this);
        }
    }

    @Override
    public void onDisable() {
        if (connectionHandler != null) {
            connectionHandler.close();
        }
    }

    @Override
    public void onReload() {
        configHandler.reload();
        if (customEconomy != null) {
            customEconomy.reload();
        }
        if (announcer == null && (boolean) configHandler.get(ConfigAddonKeys.PURCHASE_ANNOUNCER_ENABLED)) {
            common.registerListener(new Announcer(this));
        }
        if (userInfo == null && (boolean) configHandler.get(ConfigAddonKeys.USER_INFO_ENABLED)) {
            new UserInfo(this);
        }
    }

    public MineStoreCommon getCommon() {
        return common;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
