package me.chrommob.MineStoreAddons.features.economy.message;

public class EconomyResponse {
    private String id;
    private double value;
    private boolean success;
    private String message;

    public String getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public double fromValue() {
        return value;
    }

    public double toValue() {
        return value;
    }
}
