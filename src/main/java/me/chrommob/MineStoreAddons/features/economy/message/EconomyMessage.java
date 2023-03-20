package me.chrommob.MineStoreAddons.features.economy.message;

public class EconomyMessage {
    public EconomyMessage(String from, String to, double amount, boolean bypass, EconomyType type) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.bypass = bypass;
        this.type = type;
    }

    private String id;
    private String from;
    private String to;
    private double amount;
    private boolean bypass;
    private EconomyType type;

    public String setId(String id) {
        return this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public EconomyType getType() {
        return type;
    }

    public static EconomyMessage getBalance(String from) {
        return new EconomyMessage(from, null, 0, false, EconomyType.CHECK);
    }

    public static EconomyMessage pay(String from, String to, double amount) {
        return new EconomyMessage(from, to, amount, false, EconomyType.PAY);
    }

    public static EconomyMessage setBalance(String to, double amount) {
        return new EconomyMessage(null, to, amount, true, EconomyType.SET);
    }

    public static EconomyMessage deposit(String to, double amount) {
        return new EconomyMessage(null, to, amount, true, EconomyType.DEPOSIT);
    }

    public static EconomyMessage withdraw(String from, double amount) {
        return new EconomyMessage(from, null, amount, true, EconomyType.WITHDRAW);
    }
}

enum EconomyType {
    CHECK,
    PAY,
    SET,
    DEPOSIT,
    WITHDRAW
}
