package me.chrommob.MineStoreAddons.features.announcer;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreListener;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Announcer extends MineStoreListener implements SocketResponse {
    private final ConnectionHandler connectionHandler;
    private final Gson gs = new Gson();
    private String message;
    private MiniMessage mm = MiniMessage.miniMessage();
    public Announcer(MineStoreAddonsMain main) {
        //Get file as input stream
        this.message = (String) main.getConfigHandler().get(ConfigAddonKeys.PURCHASE_ANNOUNCER_FORMAT);
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
        AnnouncerResponse socketAnnouncerResponse = gs.fromJson(split[1], AnnouncerResponse.class);
        if (socketAnnouncerResponse == null) {
            return;
        }
        MineStoreCommon.getInstance().userGetter().getAllPlayers().forEach(player -> {
            String amount = String.valueOf(socketAnnouncerResponse.getAmount());
            message = message.replace("%player%", socketAnnouncerResponse.getName()).replace("%package%", socketAnnouncerResponse.getPackageName()).replace("%price%", amount);
            Component component = mm.deserialize(message);
            player.sendMessage(component);
        });
    }
}
