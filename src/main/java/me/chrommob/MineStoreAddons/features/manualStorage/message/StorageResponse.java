package me.chrommob.MineStoreAddons.features.manualStorage.message;

import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;

import java.util.Set;

public class StorageResponse {
    private String uuid;
    private Set<AnnouncerResponse> parsedResponses;

    private String command;

    public String getUuid() {
        return uuid;
    }

    public Set<AnnouncerResponse> getParsedResponses() {
        return parsedResponses;
    }

    public String getCommand() {
        return command;
    }
}
