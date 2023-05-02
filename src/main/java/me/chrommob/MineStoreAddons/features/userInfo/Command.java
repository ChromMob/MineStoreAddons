package me.chrommob.MineStoreAddons.features.userInfo;

import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import me.chrommob.minestore.libs.co.aikar.commands.BaseCommand;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.CommandAlias;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.CommandPermission;
import me.chrommob.minestore.libs.co.aikar.commands.annotation.Default;

@CommandAlias("userinfo|user")
public class Command extends BaseCommand {
    private final UserInfo userInfo;
    public Command(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Default
    @CommandPermission("minestoreaddons.userinfo.self")
    public void onSelf(AbstractUser user) {
        CommonUser commonUser = user.user();
        userInfo.sendRequest(commonUser.getName(), commonUser.getName());
    }

    @Default
    @CommandPermission("minestoreaddons.userinfo.other")
    public void onOther(AbstractUser user, String username) {
        CommonUser commonUser = user.user();
        userInfo.sendRequest(commonUser.getName(), username);
    }
}
