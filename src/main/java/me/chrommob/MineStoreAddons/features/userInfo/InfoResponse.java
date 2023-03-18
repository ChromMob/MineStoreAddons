package me.chrommob.MineStoreAddons.features.userInfo;

import java.util.HashMap;

public class InfoResponse {
    public InfoResponse(String sender, String username, double amount, HashMap<String, Integer> packageAmount, HashMap<String, Double> packagePrice) {
        this.sender = sender;
        this.username = username;
        this.amount = amount;
        this.packageAmount = packageAmount;
        this.packagePrice = packagePrice;
    }
    private String sender;
    private String username;
    private double amount;
    private HashMap<String, Integer> packageAmount;
    private HashMap<String, Double> packagePrice;

    public String getSender() {
        return sender;
    }

    public String getUsername() {
        return username;
    }

    public double getAmount() {
        return amount;
    }

    public HashMap<String, Integer> getPackageAmount() {
        return packageAmount;
    }

    public HashMap<String, Double> getPackagePrice() {
        return packagePrice;
    }
}
