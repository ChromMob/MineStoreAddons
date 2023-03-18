package me.chrommob.MineStoreAddons.features.economy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;

@SuppressWarnings("unused")
@CommandAlias("mse|minestoreeconomy")
public class EconomyCommand extends BaseCommand {
    private final CustomEconomy economy;

    public EconomyCommand(CustomEconomy economy) {
        this.economy = economy;
    }

    @Subcommand("balance")
    public void onBalance(AbstractUser user) {
        CommonUser commonUser = user.user();
        commonUser.sendMessage("Your balance is " + economy.getBalance(user.user()));
    }

    @CommandPermission("ms.economy.add.self")
    @Subcommand("add")
    public void onAdd(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        economy.addBalance(commonUser, amount);
        commonUser.sendMessage("Added " + amount + " to your balance");
    }

    @CommandPermission("ms.economy.add.other")
    @Subcommand("add")
    public void onAdd(AbstractUser user, String username, double amount) {
        CommonUser commonUser = user.user();
        CommonUser target = MineStoreCommon.getInstance().userGetter().get(username);
        if (target == null) {
            commonUser.sendMessage("User not found");
            return;
        }
        economy.addBalance(target, amount);
        commonUser.sendMessage("Added " + amount + " to " + username + "'s balance");
    }

    @CommandPermission("ms.economy.remove.self")
    @Subcommand("remove")
    public void onRemove(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        economy.removeBalance(commonUser, amount);
        commonUser.sendMessage("Removed " + amount + " from your balance");
    }

    @CommandPermission("ms.economy.remove.other")
    @Subcommand("remove")
    public void onRemove(AbstractUser user, String username, double amount) {
        CommonUser commonUser = user.user();
        CommonUser target = MineStoreCommon.getInstance().userGetter().get(username);
        if (target == null) {
            commonUser.sendMessage("User not found");
            return;
        }
        economy.removeBalance(target, amount);
        commonUser.sendMessage("Removed " + amount + " from " + username + "'s balance");
    }

    @CommandPermission("ms.economy.set.self")
    @Subcommand("set")
    public void onSet(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        economy.setBalance(commonUser, amount);
        commonUser.sendMessage("Set your balance to " + amount);
    }

    @CommandPermission("ms.economy.set.other")
    @Subcommand("set")
    public void onSet(AbstractUser user, String username, double amount) {
        CommonUser commonUser = user.user();
        CommonUser target = MineStoreCommon.getInstance().userGetter().get(username);
        if (target == null) {
            commonUser.sendMessage("User not found");
            return;
        }
        economy.setBalance(target, amount);
        commonUser.sendMessage("Set " + username + "'s balance to " + amount);
    }
}
