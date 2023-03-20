package me.chrommob.MineStoreAddons.features.economy.function;

import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.features.economy.CustomEconomy;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyMessage;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyResponse;

import java.util.UUID;

public abstract class ResponseAwaiter {
    private EconomyMessage economyMessage;


    public ResponseAwaiter(EconomyMessage economyMessage) {
        this.economyMessage = economyMessage;
        economyMessage.setId(UUID.randomUUID().toString());
        CustomEconomy.get().addAwaiter(this);
    }

    public abstract void onReceive(EconomyResponse economyResponse);

    public EconomyMessage getEconomyMessage() {
        return economyMessage;
    }
}
