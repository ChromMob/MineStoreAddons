package me.chrommob.MineStoreAddons.features.userInfo;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.gui.CommonInventory;
import me.chrommob.minestore.common.interfaces.gui.CommonItem;
import me.chrommob.minestore.libs.net.kyori.adventure.text.Component;

import java.util.*;

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
        notFound = (String) main.getConfigHandler().get(ConfigAddonKeys.USER_INFO_NOT_FOUND);
        guiName = (String) main.getConfigHandler().get(ConfigAddonKeys.USER_INFO_GUI_TITLE);
        item = (String) main.getConfigHandler().get(ConfigAddonKeys.USER_INFO_GUI_ITEM);
        main.getCommon().annotationParser().parse(new Command(this));
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
            main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(main.getCommon().miniMessage()
                    .deserialize(notFound.replaceAll("%player%", infoResponse.getUsername())));
            return;
        }
        List<CommonItem> items = new ArrayList<>();
        HashMap<String, Double> sorted = sortByValue(infoResponse.getPackagePrice());
        for (String name : sorted.keySet()) {
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Amount: " + infoResponse.getPackageAmount().get(name)));
            lore.add(Component.text("Price: " + infoResponse.getPackagePrice().get(name)));
            items.add(new CommonItem(Component.text(name), item, lore));
        }
        Component title = main.getCommon().miniMessage()
                .deserialize(guiName.replaceAll("%player%", infoResponse.getUsername()));
        main.getCommon().guiData().getGuiInfo().addCustomTitle(title);
        CommonInventory inventory = new CommonInventory(title, 54, items);
        main.getCommon().guiData().getGuiInfo().formatInventory(inventory, true);
        main.getCommon().runOnMainThread(
                () -> main.getCommon().userGetter().get(infoResponse.getSender()).openInventory(inventory));
    }

    public HashMap<String, Double> sortByValue(HashMap<String, Double> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double>> list = new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}
