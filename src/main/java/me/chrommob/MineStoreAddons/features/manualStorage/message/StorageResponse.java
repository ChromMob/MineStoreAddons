package me.chrommob.MineStoreAddons.features.manualStorage.message;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;

import java.util.HashMap;
import java.util.Map;

public class StorageResponse {
    private String uuid;
    private Map<String, Integer> parsedResponses;

    private String command;

    public String getUuid() {
        return uuid;
    }

    public Map<AnnouncerResponse, Integer> getParsedResponses() {
        return deserializedParsedResponses;
    }

    public String getCommand() {
        return command;
    }

    private transient Map<AnnouncerResponse, Integer> deserializedParsedResponses;

    public void parse(Gson gson) {
        deserializedParsedResponses = new HashMap<>();
        for (Map.Entry<String, Integer> entry : parsedResponses.entrySet()) {
            AnnouncerResponse response = gson.fromJson(entry.getKey(), AnnouncerResponse.class);
            deserializedParsedResponses.put(response, entry.getValue());
        }
    }
}
