package me.chrommob.MineStoreAddons.features.manualStorage;

import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.features.announcer.AnnouncerResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.gui.CommonInventory;
import me.chrommob.minestore.common.interfaces.gui.CommonItem;
import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import me.chrommob.minestore.libs.co.aikar.commands.BaseCommand;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.CommandAlias;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.CommandPermission;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.Default;
import me.chrommob.minestore.libs.net.kyori.adventure.text.Component;
import me.chrommob.minestore.libs.net.kyori.adventure.text.format.NamedTextColor;
import me.chrommob.minestore.libs.net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@CommandAlias("redeem")
@CommandPermission("ms.redeem")
public class RedeemCommand extends BaseCommand {
    private ManualCommandStorage manualCommandStorage;
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    private MineStoreAddonsMain main;
    private Component title;
    public RedeemCommand(ManualCommandStorage manualCommandStorage, MineStoreAddonsMain main) {
        this.manualCommandStorage = manualCommandStorage;
        this.main = main;
        this.title = miniMessage.deserialize((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_GUI_TITLE));
        main.getCommon().guiData().getGuiInfo().addCustomTitle(title);
    }

    @Default
    public void onRedeem(AbstractUser user) {
        CommonUser commonUser = MineStoreCommon.getInstance().userGetter().get(user.user().getName());
        new StorageRequest(user.user().getName()) {
            @Override
            public void onResponse(Map<AnnouncerResponse, Integer> parsedResponses, String command) {
                if (parsedResponses == null || parsedResponses.isEmpty()) {
                    commonUser.sendMessage(miniMessage.deserialize((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_NO_PACKAGES)));
                    return;
                }
                List<CommonItem> commonItems = new ArrayList<>();
                for (AnnouncerResponse parsedResponse : parsedResponses.keySet()) {
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text("Click to redeem!").color(NamedTextColor.GREEN));
                    int count = parsedResponses.get(parsedResponse);
                    CommonItem commonItem = new CommonItem(Component.text(parsedResponse.getPackageName()).color(NamedTextColor.AQUA), (String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_GUI_ITEM), lore, count);
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

    public void reload() {
        this.title = miniMessage.deserialize((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_GUI_TITLE));
        main.getCommon().guiData().getGuiInfo().addCustomTitle(title);
    }
}
