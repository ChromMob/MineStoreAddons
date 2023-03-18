package me.chrommob.MineStoreAddons;

import me.chrommob.MineStoreAddons.features.announcer.Announcer;
import me.chrommob.MineStoreAddons.features.userInfo.UserInfo;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreAddon;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class MineStoreAddonsMain extends MineStoreAddon {
    private MineStoreCommon common;
    private ConnectionHandler connectionHandler;
    private File configFile;
    private LinkedHashMap<String, Object> config;
    private Yaml yaml = new Yaml();
    private Announcer announcer;
    private UserInfo userInfo;
    @Override
    public void onEnable() {
        common = MineStoreCommon.getInstance();
        configFile = new File(MineStoreCommon.getInstance().configFile().getParentFile() + File.separator + "addons" + File.separator + "MineStoreAddons" + File.separator + "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            try {
                Files.copy(getClass().getClassLoader().getResourceAsStream("config.yml"), configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = yaml.load(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        Map<String, Object> announcer = (Map<String, Object>) config.get("purchase-announcer");
        if ((boolean) announcer.get("enabled")) {
            connectionHandler.registerSocketResponse(this.announcer);
        }
        Map<String, Object> userInfo = (Map<String, Object>) config.get("user-info");
        if ((boolean) userInfo.get("enabled")) {
            connectionHandler.registerSocketResponse(this.userInfo);
        }
    }

    private void registerListeners() {
        Map<String, Object> announcer = (Map<String, Object>) config.get("purchase-announcer");
        if ((boolean) announcer.get("enabled")) {
            this.announcer = new Announcer(this);
            common.registerListener(new Announcer(this));
        }
        Map<String, Object> userInfo = (Map<String, Object>) config.get("user-info");
        if ((boolean) userInfo.get("enabled")) {
            this.userInfo = new UserInfo(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onReload() {
        if (announcer == null && (boolean) ((Map<String, Object>) config.get("purchase-announcer")).get("enabled")) {
            common.registerListener(new Announcer(this));
        }
        if (userInfo == null && (boolean) ((Map<String, Object>) config.get("user-info")).get("enabled")) {
            new UserInfo(this);
        }
    }

    public MineStoreCommon getCommon() {
        return common;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}
