package me.chrommob.MineStoreAddons;

import me.chrommob.MineStoreAddons.features.Announcer;
import me.chrommob.MineStoreAddons.socket.ConnectionHandler;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.addons.MineStoreAddon;

@SuppressWarnings("unused")
public class MineStoreAddonsMain extends MineStoreAddon {
    private MineStoreCommon common;
    private ConnectionHandler connectionHandler;
    @Override
    public void onEnable() {
        common = MineStoreCommon.getInstance();
        connectionHandler = new ConnectionHandler();
        registerListeners();
    }

    private void registerListeners() {
        common.registerListener(new Announcer(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onReload() {
        // Plugin reload logic
    }

    public MineStoreCommon getCommon() {
        return common;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public void registerSocketResponse(SocketResponse socketResponse) {
        connectionHandler.registerSocketResponse(socketResponse);
    }
}
