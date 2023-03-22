package me.chrommob.MineStoreAddons.features.manualStorage;

import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;

import java.util.Set;

public abstract class StorageRequest {
    public StorageRequest(String name) {
        this.name = name;
        this.uuid = java.util.UUID.randomUUID().toString();
        ManualCommandStorage.getInstance().addStorageResponse(this);
    }
    private String uuid;
    private String name;

    public abstract void onResponse(Set<ParsedResponse> parsedResponses);

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
