package me.chrommob.MineStoreAddons.features.economy;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.features.economy.function.ResponseAwaiter;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyMessage;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyResponse;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.economyInfo.PlayerEconomyProvider;
import me.chrommob.minestore.common.interfaces.user.CommonUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomEconomy implements PlayerEconomyProvider, SocketResponse {
    private final MineStoreAddonsMain main;
    private static CustomEconomy instance;
    private EconomyCommand economyCommand;
    private final Gson gson = new Gson();
    public CustomEconomy(MineStoreAddonsMain main) {
        instance = this;
        this.main = main;
    }

    public void reload() {
        if (economyCommand != null)
            economyCommand.loadConfig();
    }

    @Override
    public void onResponse(String response) {
        if (response.startsWith("economy-")) {
            EconomyResponse economyResponse = gson.fromJson(response.substring(8), EconomyResponse.class);
            ResponseAwaiter responseAwaiter = responseAwaiterMap.get(economyResponse.getId());
            if (responseAwaiter != null) {
                EconomyMessage economyMessage = responseAwaiter.getEconomyMessage();
                if (economyMessage.getFrom() != null)
                    balanceCache.put(economyMessage.getFrom(), economyResponse.fromValue());
                if (economyMessage.getTo() != null)
                    balanceCache.put(economyMessage.getTo(), economyResponse.toValue());
                responseAwaiter.onReceive(economyResponse);
                responseAwaiterMap.remove(economyResponse.getId());
            }
        }
    }

    private Map<String, Double> balanceCache = new ConcurrentHashMap<>();
    private Map<String, ResponseAwaiter> responseAwaiterMap = new ConcurrentHashMap<>();
    @Override
    public double getBalance(CommonUser commonUser) {
        return balanceCache.getOrDefault(commonUser.getName(), 0.0);
    }

    public void onEnable() {
        economyCommand = new EconomyCommand(this, main);
        MineStoreCommon.getInstance().commandManager().registerCommand(economyCommand);
    }

    public void addAwaiter(ResponseAwaiter responseAwaiter) {
        responseAwaiterMap.put(responseAwaiter.getEconomyMessage().getId(), responseAwaiter);
        main.getConnectionHandler().addMessage("economy-" + gson.toJson(responseAwaiter.getEconomyMessage()));
    }

    public static CustomEconomy get() {
        return instance;
    }
}
