package me.chrommob.MineStoreAddons.features.manualStorage;

import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;

import java.util.Set;

public abstract class StorageRequest {
    public StorageRequest(String name) {
        this.name = name;
        this.uuid = java.util.UUID.randomUUID().toString();
        ManualCommandStorage.getInstance().addStorageResponse(this);
    }

    public StorageRequest(String name, String packageName) {
        this.name = name;
        this.uuid = java.util.UUID.randomUUID().toString();
        this.packageName = packageName;
        ManualCommandStorage.getInstance().addStorageResponse(this);
    }

    private String uuid;
    private String name;
    private String packageName;

    public abstract void onResponse(Set<AnnouncerResponse> parsedResponses, String command);

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
