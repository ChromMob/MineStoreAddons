package me.chrommob.MineStoreAddons.features;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreListener;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;

public class Announcer extends MineStoreListener implements SocketResponse {
    private final ConnectionHandler connectionHandler;
    private final Gson gs = new Gson();
    public Announcer(MineStoreAddonsMain main) {
        main.registerSocketResponse(this);
        this.connectionHandler = main.getConnectionHandler();
    }
    @Override
    public void onPurchase(ParsedResponse event) {
        connectionHandler.addMessage("announcer-" + gs.toJson(event));
    }

    @Override
    public void onResponse(String response) {
        String[] split = response.split("-");
        if (!split[0].equalsIgnoreCase("announcer")) {
            return;
        }
        Response socketResponse = gs.fromJson(split[1], Response.class);
        if (socketResponse == null) {
            return;
        }
        MineStoreCommon.getInstance().userGetter().getAllPlayers().forEach(player -> {
            player.sendMessage(socketResponse.getName() + " just purchased " + socketResponse.getPackageName() + " for " + socketResponse.getAmount() + "!");
        });
    }
}
