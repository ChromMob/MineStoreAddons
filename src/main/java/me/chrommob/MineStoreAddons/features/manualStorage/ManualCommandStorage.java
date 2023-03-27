package me.chrommob.MineStoreAddons.features.manualStorage;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.addons.MineStoreListener;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;
import me.chrommob.minestore.common.interfaces.commands.CommandStorageInterface;
import me.chrommob.minestore.common.interfaces.gui.CommonItem;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ManualCommandStorage extends MineStoreListener implements CommandStorageInterface, SocketResponse {
    private final MineStoreAddonsMain main;
    private static ManualCommandStorage instance;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Gson gson = new Gson();
    public ManualCommandStorage(MineStoreAddonsMain mineStoreAddonsMain) {
        instance = this;
        this.main = mineStoreAddonsMain;
        main.getCommon().commandManager().registerCommand(new RedeemCommand(this, main));
    }

    @Override
    public void onResponse(String response) {
        if (response == null || response.isEmpty()) return;
        if (!response.startsWith("storage-")) return;
        response = response.replace("storage-", "");
        StorageResponse storageResponse = gson.fromJson(response, StorageResponse.class);
        if (storageResponse == null) return;
        StorageRequest storageRequest = requests.get(storageResponse.getUuid());
        if (storageRequest == null) return;
        storageRequest.onResponse(storageResponse.getParsedResponses());
        requests.remove(storageResponse.getUuid());
    }

    private Map<String, StorageRequest> requests = new ConcurrentHashMap<>();
    public void addStorageResponse(StorageRequest request) {
        if (main.getConnectionHandler() == null) return;
        requests.put(request.getUuid(), request);
        main.getConnectionHandler().addMessage("storage-" + request.getName() + ":" + request.getUuid());
    }

    @Override
    public void onPlayerJoin(String username) {
        if (main.getConnectionHandler() == null) return;
        if (!(boolean) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_MESSAGE_ENABLED)) return;
        main.getConnectionHandler().addMessage("storage-" + gson.toJson(new StorageRequest(username) {
            @Override
            public void onResponse(Set<ParsedResponse> parsedResponses) {
                if (parsedResponses == null || parsedResponses.isEmpty()) return;
                CommonUser user = main.getCommon().userGetter().get(username);
                if (user == null) return;
                user.sendMessage(miniMessage.deserialize(((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_MESSAGE_FORMAT)).replace("%amount%", parsedResponses.size() + "")));
            }
        }));
    }

    private Set<String> commands = new HashSet<>();
    @Override
    public void listener(ParsedResponse command) {
        if (main.getConnectionHandler() == null) {
            commands.add(gson.toJson(command));
            return;
        }
        if (commands.size() > 0) {
            commands.forEach(command1 -> main.getConnectionHandler().addMessage("storage-" + command1));
            commands.clear();
        }
        main.getConnectionHandler().addMessage("storage-" + gson.toJson(command));
    }

    @Override
    public void onClick(CommonItem item, CommonUser commonUser, Component title) {

    }

    @Override
    public void init() {}

    public static ManualCommandStorage getInstance() {
        return instance;
    }
}
