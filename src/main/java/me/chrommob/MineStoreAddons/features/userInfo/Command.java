package me.chrommob.MineStoreAddons.features.userInfo;

import org.checkerframework.checker.units.qual.C;

import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import me.chrommob.minestore.libs.cloud.commandframework.annotations.Argument;
import me.chrommob.minestore.libs.cloud.commandframework.annotations.CommandMethod;
import me.chrommob.minestore.libs.cloud.commandframework.annotations.CommandPermission;

public class Command {
    private final UserInfo userInfo;

    public Command(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @CommandMethod("userinfo|user")
    @CommandPermission("minestoreaddons.userinfo.self")
    public void onSelf(AbstractUser user) {
        CommonUser commonUser = user.user();
        userInfo.sendRequest(commonUser.getName(), commonUser.getName());
    }

    @CommandMethod("userinfo|user <username>")
    @CommandPermission("minestoreaddons.userinfo.other")
    public void onOther(AbstractUser user, @Argument("username") String username) {
        CommonUser commonUser = user.user();
        userInfo.sendRequest(commonUser.getName(), username);
    }
}
