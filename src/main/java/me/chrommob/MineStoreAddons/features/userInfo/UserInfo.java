package me.chrommob.MineStoreAddons.features.userInfo;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.gui.CommonInventory;
import me.chrommob.minestore.common.interfaces.gui.CommonItem;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserInfo implements SocketResponse {
    private final MineStoreAddonsMain main;
    private String notFound;
    private String guiName;
    private String item;
    private final Gson gs = new Gson();
    public UserInfo(MineStoreAddonsMain main) {
        this.main = main;
        init();
    }

    private void init() {
        Map<String, Object> userInfo = (Map<String, Object>) main.getConfig().get("user-info");
        notFound = (String) userInfo.get("not-found");
        Map<String, Object> gui = (Map<String, Object>) userInfo.get("gui");
        guiName = (String) gui.get("title");
        item = (String) gui.get("item");
        main.registerSocketResponse(this);
        main.getCommon().commandManager().registerCommand(new Command(this));
    }

    public void sendRequest(String sender, String username) {
        main.getConnectionHandler().addMessage("userinfo-" + sender + ":" + username);
    }

    @Override
    public void onResponse(String response) {
        String[] split = response.split("-");
        if (!split[0].equalsIgnoreCase("userinfo")) {
            return;
        }
        InfoResponse infoResponse = gs.fromJson(response.replace(split[0] + "-", ""), InfoResponse.class);
        if (infoResponse == null) {
            MineStoreCommon.getInstance().log("Error while parsing userinfo response");
            return;
        }
        if (infoResponse.getPackageAmount().isEmpty()) {
            main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(main.getCommon().miniMessage().deserialize(notFound.replaceAll("%player%", infoResponse.getUsername())));
            return;
        }
        List<CommonItem> items = new ArrayList<>();
        for (String name : infoResponse.getPackageAmount().keySet()) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Amount: " + infoResponse.getPackageAmount().get(name)));
            lore.add(Component.text("Price: " + infoResponse.getPackagePrice().get(name)));
            items.add(new CommonItem(Component.text(name), item, lore));
        }
        Component title = main.getCommon().miniMessage().deserialize(guiName.replaceAll("%player%", infoResponse.getUsername()));
        main.getCommon().guiData().getGuiInfo().addCustomTitle(title);
        CommonInventory inventory =  new CommonInventory(title, 54, items);
        main.getCommon().runOnMainThread(() -> main.getCommon().userGetter().get(infoResponse.getSender()).openInventory(inventory));
    }
}
