package me.chrommob.MineStoreAddons.features.manualStorage;

import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;

import java.util.Set;

public class StorageResponse {
    private String uuid;
    private Set<ParsedResponse> parsedResponses;

    public String getUuid() {
        return uuid;
    }

    public Set<ParsedResponse> getParsedResponses() {
        return parsedResponses;
    }
}
