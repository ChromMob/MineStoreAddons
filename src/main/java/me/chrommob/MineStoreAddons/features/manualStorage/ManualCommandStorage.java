package me.chrommob.MineStoreAddons.features.manualStorage;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;
import me.chrommob.MineStoreAddons.features.manualStorage.message.StorageResponse;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.addons.MineStoreListener;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;
import me.chrommob.minestore.common.interfaces.commands.CommandStorageInterface;
import me.chrommob.minestore.common.interfaces.gui.CommonInventory;
import me.chrommob.minestore.common.interfaces.gui.CommonItem;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import me.chrommob.minestore.libs.net.kyori.adventure.text.Component;
import me.chrommob.minestore.libs.net.kyori.adventure.text.format.NamedTextColor;
import me.chrommob.minestore.libs.net.kyori.adventure.text.minimessage.MiniMessage;
import me.chrommob.minestore.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import me.chrommob.minestore.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ManualCommandStorage extends MineStoreListener implements CommandStorageInterface, SocketResponse {
    private final MineStoreAddonsMain main;
    private static ManualCommandStorage instance;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private Gson gson = new Gson();
    private RedeemCommand redeemCommand;
    public ManualCommandStorage(MineStoreAddonsMain mineStoreAddonsMain) {
        instance = this;
        this.main = mineStoreAddonsMain;
        redeemCommand = new RedeemCommand(this, main);
        main.getCommon().commandManager().registerCommand(redeemCommand);
        main.getCommon().registerListener(this);
    }

    @Override
    public void onResponse(String response) {
        if (response == null || response.isEmpty()) return;
        if (!response.startsWith("storage-")) return;
        response = response.replace("storage-", "");
        StorageResponse storageResponse = gson.fromJson(response, StorageResponse.class);
        if (storageResponse == null) return;
        storageResponse.parse(gson);
        StorageRequest storageRequest = requests.get(storageResponse.getUuid());
        if (storageRequest == null) return;
        storageRequest.onResponse(storageResponse.getParsedResponses(), storageResponse.getCommand());
        requests.remove(storageResponse.getUuid());
    }

    private Map<String, StorageRequest> requests = new ConcurrentHashMap<>();
    public void addStorageResponse(StorageRequest request) {
        if (main.getConnectionHandler() == null) return;
        requests.put(request.getUuid(), request);
        if (request.getPackageName() != null) {
            main.getConnectionHandler().addMessage("storage-" + request.getName() + ":" + request.getUuid() + ":" + request.getPackageName());
            return;
        }
        main.getConnectionHandler().addMessage("storage-" + request.getName() + ":" + request.getUuid());
    }

    @Override
    public void onPlayerJoin(String username) {
        if (main.getConnectionHandler() == null) return;
        if (username == null || username.isEmpty()) return;
        if (!(boolean) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_MESSAGE_ENABLED)) return;
        main.getConnectionHandler().addMessage("storage-" + gson.toJson(new StorageRequest(username) {
            @Override
            public void onResponse(Map<AnnouncerResponse, Integer> parsedResponses, String command) {
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
        if (!(boolean) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_MESSAGE_ENABLED)) {
            return;
        }
        String username = command.username();
        if (username == null || username.isEmpty()) return;
        CommonUser user = main.getCommon().userGetter().get(username);
        if (user == null) {
            return;
        }
        if (!user.isOnline()) {
            return;
        }
        main.getConnectionHandler().addMessage("storage-" + gson.toJson(new StorageRequest(username) {
            @Override
            public void onResponse(Map<AnnouncerResponse, Integer> parsedResponses, String command) {
                if (parsedResponses == null || parsedResponses.isEmpty()) return;
                CommonUser user = main.getCommon().userGetter().get(username);
                if (user == null) return;
                user.sendMessage(miniMessage.deserialize(((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_MESSAGE_FORMAT)).replace("%amount%", parsedResponses.size() + "")));
            }
        }));
    }

    @Override
    public void onClick(CommonItem item, CommonUser commonUser, Component title) {
        if (main.getConnectionHandler() == null) return;
        String titleString = LegacyComponentSerializer.legacySection().serialize(title);
        Component component = miniMessage.deserialize(((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_GUI_TITLE)));
        String componentString = LegacyComponentSerializer.legacySection().serialize(component);
        if (!titleString.equals(componentString)) return;
        String packageName = PlainTextComponentSerializer.plainText().serialize(item.getName());
        if (packageName.isEmpty() || packageName == " " || packageName == null) return;
        new StorageRequest(commonUser.getName(), packageName) {
            @Override
            public void onResponse(Map<AnnouncerResponse, Integer> parsedResponses, String command) {
                if (command == null || command.isEmpty() ||parsedResponses == null || parsedResponses.isEmpty()) {
                    CommonInventory commonInventory = new CommonInventory(title, 54, new ArrayList<>());
                    main.getCommon().guiData().getGuiInfo().formatInventory(commonInventory, true);
                    main.getCommon().runOnMainThread(() -> {
                        commonUser.openInventory(commonInventory);
                    });
                }
                main.getCommon().commandExecuter().execute(command);
                List<CommonItem> commonItems = new ArrayList<>();
                for (AnnouncerResponse parsedResponse : parsedResponses.keySet()) {
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("Click to redeem!").color(NamedTextColor.GREEN));
                    int amount = parsedResponses.get(parsedResponse);
                    CommonItem commonItem = new CommonItem(Component.text(parsedResponse.getPackageName()).color(NamedTextColor.AQUA), (String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_GUI_ITEM), lore, amount);
                    commonItems.add(commonItem);
                }
                CommonInventory commonInventory = new CommonInventory(title, 54, commonItems);
                main.getCommon().guiData().getGuiInfo().formatInventory(commonInventory, true);
                main.getCommon().runOnMainThread(() -> {
                    commonUser.openInventory(commonInventory);
                });
            }
        };
    }


    @Override
    public void init() {}

    public static ManualCommandStorage getInstance() {
        return instance;
    }

    public void reload() {
        redeemCommand.reload();
    }
}
