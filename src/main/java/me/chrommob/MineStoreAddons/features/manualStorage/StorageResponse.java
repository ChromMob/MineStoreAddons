package me.chrommob.MineStoreAddons.features.manualStorage;

import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;

import java.util.Set;

public class StorageResponse {
    private String uuid;
    private Set<AnnouncerResponse> parsedResponses;

    public String getUuid() {
        return uuid;
    }

    public Set<AnnouncerResponse> getParsedResponses() {
        return parsedResponses;
    }
}
