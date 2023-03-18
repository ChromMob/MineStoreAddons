package me.chrommob.MineStoreAddons.features.economy;

import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.economyInfo.PlayerEconomyProvider;
import me.chrommob.minestore.common.interfaces.user.CommonUser;

import java.util.HashMap;

public class CustomEconomy implements PlayerEconomyProvider {
    private HashMap<String, Double> balances = new HashMap<>();

    @Override
    public double getBalance(CommonUser commonUser) {
        return balances.getOrDefault(commonUser.getName(), 0.0);
    }

    public void addBalance(CommonUser commonUser, double amount) {
        balances.put(commonUser.getName(), balances.getOrDefault(commonUser, 0.0) + amount);
    }

    public void removeBalance(CommonUser commonUser, double amount) {
        balances.put(commonUser.getName(), balances.getOrDefault(commonUser, 0.0) - amount);
    }

    public void setBalance(CommonUser commonUser, double amount) {
        balances.put(commonUser.getName(), amount);
    }

    public void onEnable() {
        MineStoreCommon.getInstance().commandManager().registerCommand(new EconomyCommand(this));
    }
}
