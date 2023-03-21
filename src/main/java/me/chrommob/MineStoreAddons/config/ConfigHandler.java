package me.chrommob.MineStoreAddons.config;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.config.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigHandler {
    private File configFile;
    private Yaml yaml;
    private Map<String, Object> configYaml = new LinkedHashMap<>();
    public ConfigHandler(File configFile) {
        this.configFile = configFile;
        init();
    }

    public void init() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        yaml = new Yaml(options);
        if (!configFile.exists()) {
            populateDefaultConfig();
            configFile.getParentFile().mkdirs();
            saveDefaultConfig();
        }
        reload();
    }

    private void populateDefaultConfig() {
        for (ConfigAddonKeys key : ConfigAddonKeys.values()) {
            Configuration configuration = key.getConfiguration();
            String location = configuration.getLocation();
            if (location.split("\\.").length > 1) {
                String[] split = location.split("\\.");
                Map<String, Object> currentLocation = null;
                for (int i = 0; i < split.length; i++) {
                    boolean isLast = i >= split.length - 1;
                    if (i == 0) {
                        configYaml.putIfAbsent(split[i], new LinkedHashMap());
                        currentLocation = (Map<String, Object>) configYaml.get(split[i]);
                    } else if (!isLast) {
                        currentLocation.putIfAbsent(split[i], new LinkedHashMap());
                        currentLocation = (Map<String, Object>) currentLocation.get(split[i]);
                    } else {
                        currentLocation.putIfAbsent(split[i], configuration.getDefaultValue());
                    }
                }
            } else {
                configYaml.put(location, configuration.getDefaultValue());
            }
        }
    }

    private void saveDefaultConfig() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            yaml.dump(configYaml, writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
            configYaml = yaml.load(reader);
        } catch (Exception e) {
            MineStoreCommon.getInstance().debug(e);
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e) {
            MineStoreCommon.getInstance().debug(e);
        }
        for (final ConfigAddonKeys key : ConfigAddonKeys.values()) {
            final Configuration configuration = key.getConfiguration();
            final String location = configuration.getLocation();
            final String[] split = location.split("\\.");
            if (split.length > 1) {
                Map<String, Object> currentLocation = null;
                for (int i = 0; i < split.length; i++) {
                    boolean isLast = i == split.length - 1;
                    if (i == 0) {
                        if (!configYaml.containsKey(split[i])) {
                            configYaml.put(split[i], new LinkedHashMap());
                        }
                        currentLocation = (Map<String, Object>) configYaml.get(split[i]);
                    } else if (!isLast) {
                        if (!currentLocation.containsKey(split[i])) {
                            currentLocation.put(split[i], new LinkedHashMap());
                        }
                        currentLocation = (Map<String, Object>) currentLocation.get(split[i]);
                    } else {
                        if (!currentLocation.containsKey(split[i])) {
                            currentLocation.put(split[i], configuration.getDefaultValue());
                        }
                    }
                }
            }
            else if (!configYaml.containsKey(location)) {
                configYaml.put(location, configuration.getDefaultValue());
            }
        }
        saveDefaultConfig();
    }

    public Object get(ConfigAddonKeys key) {
        Configuration configuration = key.getConfiguration();
        String location = configuration.getLocation();
        if (location.split("\\.").length > 1) {
            String[] split = location.split("\\.");
            Map<String, Object> currentLocation = null;
            for (int i = 0; i < split.length; i++) {
                boolean isLast = i == split.length - 1;
                if (i == 0) {
                    currentLocation = (Map<String, Object>) configYaml.get(split[i]);
                } else if (!isLast) {
                    currentLocation = (Map<String, Object>) currentLocation.get(split[i]);
                } else {
                    return currentLocation.get(split[i]);
                }
            }
        }
        return configYaml.get(location);
    }
}
