package me.chrommob.MineStoreAddons.features.userInfo;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;

@CommandAlias("userinfo|user")
public class Command extends BaseCommand {
    private final UserInfo userInfo;
    public Command(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    @CommandPermission("minestoreaddons.userinfo.self")
    public void onSelf(AbstractUser user) {
        CommonUser commonUser = user.user();
        commonUser.sendMessage("Your username is " + commonUser.getName());
    }

    @CommandPermission("minestoreaddons.userinfo.other")
    public void onOther(AbstractUser user, String username) {
        CommonUser commonUser = user.user();
        commonUser.sendMessage(username + "'s username is " + username);
    }
}
