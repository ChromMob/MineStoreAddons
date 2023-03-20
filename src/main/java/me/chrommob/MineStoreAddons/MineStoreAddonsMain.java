package me.chrommob.MineStoreAddons;

import me.chrommob.MineStoreAddons.features.announcer.Announcer;
import me.chrommob.MineStoreAddons.features.economy.CustomEconomy;
import me.chrommob.MineStoreAddons.features.userInfo.UserInfo;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreAddon;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class MineStoreAddonsMain extends MineStoreAddon {
    private MineStoreCommon common;
    private ConnectionHandler connectionHandler;
    private File configFile;
    private Map<String, Object> config = new LinkedHashMap<>();
    private Yaml yaml;
    private Announcer announcer;
    private UserInfo userInfo;
    private CustomEconomy customEconomy;

    @Override
    public void onLoad() {
        common = MineStoreCommon.getInstance();
        configFile = new File(MineStoreCommon.getInstance().configFile().getParentFile() + File.separator + "addons" + File.separator + "MineStoreAddons" + File.separator + "config.yml");
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            createConfig();
            try {
                yaml.dump(config, new FileWriter(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = yaml.load(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
       Map<String, Object> economyConfig = (Map<String, Object>) config.get("economy");
        if ((boolean) economyConfig.get("enabled")) {
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

    private void createConfig() {
        config.put("purchase-announcer", new LinkedHashMap<String, Object>() {{
            put("enabled", true);
            put("format", "<red><bold>%player%</bold></red> bought <bold>%package%</bold> for <gold>%price%");
        }});
        config.put("user-info", new LinkedHashMap<String, Object>() {{
            put("enabled", true);
            put("not-found", "<red>%player% did not buy any packages yet.");
            put("gui", new LinkedHashMap<String, Object>() {{
                put("title", "<gold>%player%'s packages");
                put("item", "PAPER");
            }});
        }});
        config.put("economy", new LinkedHashMap<String, Object>() {{
            put("enabled", true);
        }});
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
        Map<String, Object> economy = (Map<String, Object>) config.get("economy");
        if ((boolean) economy.get("enabled")) {
            connectionHandler.registerSocketResponse(customEconomy);
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
        if (connectionHandler != null) {
            connectionHandler.close();
        }
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
