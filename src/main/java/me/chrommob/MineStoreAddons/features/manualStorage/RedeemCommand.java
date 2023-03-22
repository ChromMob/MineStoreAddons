package me.chrommob.MineStoreAddons.features.manualStorage;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.commandGetters.dataTypes.ParsedResponse;
import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Set;

@SuppressWarnings("unused")
@CommandAlias("redeem")
@CommandPermission("minestoreaddons.redeem")
public class RedeemCommand extends BaseCommand {
    private ManualCommandStorage manualCommandStorage;
    private MiniMessage miniMessage = MiniMessage.miniMessage();
    private MineStoreAddonsMain main;
    public RedeemCommand(ManualCommandStorage manualCommandStorage, MineStoreAddonsMain main) {
        this.manualCommandStorage = manualCommandStorage;
        this.main = main;
    }

    @Default
    public void onRedeem(AbstractUser user) {
        CommonUser commonUser = MineStoreCommon.getInstance().userGetter().get(user.user().getName());
        manualCommandStorage.addStorageResponse(new StorageRequest(user.user().getName()) {
            @Override
            public void onResponse(Set<ParsedResponse> parsedResponses) {
                if (parsedResponses == null || parsedResponses.isEmpty()) {
                    commonUser.sendMessage(miniMessage.deserialize((String) main.getConfigHandler().get(ConfigAddonKeys.MANUAL_REDEEM_NO_PACKAGES)));
                    return;
                }
                commonUser.sendMessage("Storage response: " + parsedResponses.size() + " packages found");
            }
        });
    }
}
