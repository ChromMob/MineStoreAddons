package me.chrommob.MineStoreAddons;

import me.chrommob.MineStoreAddons.features.Announcer;
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
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MineStoreAddonsMain extends MineStoreAddon {
    private MineStoreCommon common;
    private ConnectionHandler connectionHandler;
    private File configFile;
    private LinkedHashMap<String, Object> config;
    private Yaml yaml = new Yaml();
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
        connectionHandler = new ConnectionHandler("ws://ws.chrommob.fun:8080", this);
        connectionHandler.connect();
        registerListeners();
    }

    private boolean announcePurchases = false;
    private void registerListeners() {
        Map<String, Object> announcer = (Map<String, Object>) config.get("purchase-announcer");
        if ((boolean) announcer.get("enabled")) {
            announcePurchases = true;
            common.registerListener(new Announcer(this));
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onReload() {
        if (!announcePurchases && (boolean) ((Map<String, Object>) config.get("purchase-announcer")).get("enabled")) {
            common.registerListener(new Announcer(this));
        }
    }

    public MineStoreCommon getCommon() {
        return common;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void registerSocketResponse(SocketResponse socketResponse) {
        connectionHandler.registerSocketResponse(socketResponse);
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}
