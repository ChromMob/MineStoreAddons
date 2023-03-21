package me.chrommob.MineStoreAddons.features.economy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.config.ConfigAddonKeys;
import me.chrommob.MineStoreAddons.features.economy.function.ResponseAwaiter;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyMessage;
import me.chrommob.MineStoreAddons.features.economy.message.EconomyResponse;
import me.chrommob.minestore.common.MineStoreCommon;
import me.chrommob.minestore.common.interfaces.user.AbstractUser;
import me.chrommob.minestore.common.interfaces.user.CommonUser;
import net.kyori.adventure.text.minimessage.MiniMessage;

@SuppressWarnings("unused")
@CommandAlias("mse|minestoreeconomy")
public class EconomyCommand extends BaseCommand {
    private final CustomEconomy economy;
    private final MineStoreAddonsMain main;
    private String balanceFormat;
    private String addFormat;
    private String addFormatOther;
    private String removeFormat;
    private String removeFormatOther;
    private String setFormat;
    private String setFormatOther;
    private String sendFormat;
    private String sendFormatOther;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public EconomyCommand(CustomEconomy economy, MineStoreAddonsMain main) {
        this.economy = economy;
        this.main = main;
        loadConfig();
    }

    private void loadConfig() {
        balanceFormat = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_BALANCE_FORMAT);
        addFormat = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_ADD_FORMAT_SELF);
        addFormatOther = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_ADD_FORMAT_OTHER);
        removeFormat = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_REMOVE_FORMAT_SELF);
        removeFormatOther = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_REMOVE_FORMAT_OTHER);
        setFormat = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_SET_FORMAT_SELF);
        setFormatOther = (String) main.getConfigHandler().get(ConfigAddonKeys.ECONOMY_SET_FORMAT_OTHER);
    }

    @Subcommand("balance")
    public void onBalance(AbstractUser user) {
        CommonUser commonUser = user.user();
        EconomyMessage economyMessage = EconomyMessage.getBalance(commonUser.getName());
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(balanceFormat.replace("%balance%", String.valueOf(economyResponse.fromValue()))));
                else
                    commonUser.sendMessage("Failed to get your balance. Reason: " + economyResponse.getMessage());
            }
        };
    }

    @CommandPermission("ms.economy.add.self")
    @Subcommand("add")
    public void onAdd(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        EconomyMessage economyMessage = EconomyMessage.deposit(commonUser.getName(), amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(addFormat.replace("%amount%", String.valueOf(amount))));
                else
                    commonUser.sendMessage("Failed to add " + amount + " to your balance. Reason: " + economyResponse.getMessage());
            }
        };
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
        EconomyMessage economyMessage = EconomyMessage.deposit(username, amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(addFormatOther.replace("%amount%", String.valueOf(amount)).replace("%player%", username)));
                else
                    commonUser.sendMessage("Failed to add " + amount + " to " + username + "'s balance. Reason: " + economyResponse.getMessage());
            }
        };
    }

    @CommandPermission("ms.economy.remove.self")
    @Subcommand("remove")
    public void onRemove(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        EconomyMessage economyMessage = EconomyMessage.withdraw(commonUser.getName(), amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(removeFormat.replace("%amount%", String.valueOf(amount))));
                else
                    commonUser.sendMessage("Failed to remove " + amount + " from your balance. Reason: " + economyResponse.getMessage());
            }
        };
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
        EconomyMessage economyMessage = EconomyMessage.withdraw(username, amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(removeFormatOther.replace("%amount%", String.valueOf(amount)).replace("%player%", username)));
                else
                    commonUser.sendMessage("Failed to remove " + amount + " from " + username + "'s balance. Reason: " + economyResponse.getMessage());
            }
        };
    }

    @CommandPermission("ms.economy.set.self")
    @Subcommand("set")
    public void onSet(AbstractUser user, double amount) {
        CommonUser commonUser = user.user();
        EconomyMessage economyMessage = EconomyMessage.setBalance(commonUser.getName(), amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(setFormat.replace("%amount%", String.valueOf(amount))));
                else
                    commonUser.sendMessage("Failed to set your balance to " + amount + ". Reason: " + economyResponse.getMessage());
            }
        };
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
        EconomyMessage economyMessage = EconomyMessage.setBalance(username, amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess())
                    commonUser.sendMessage(miniMessage.deserialize(setFormatOther.replace("%amount%", String.valueOf(amount)).replace("%player%", username)));
                else
                    commonUser.sendMessage("Failed to set " + username + "'s balance to " + amount + ". Reason: " + economyResponse.getMessage());
            }
        };
    }

    @CommandPermission("ms.economy.send")
    @Subcommand("send")
    public void onSend(AbstractUser user, String username, double amount) {
        CommonUser commonUser = user.user();
        CommonUser target = MineStoreCommon.getInstance().userGetter().get(username);
        if (username.equalsIgnoreCase(commonUser.getName())) {
            commonUser.sendMessage("You can't send money to yourself");
            return;
        }
        if (target == null) {
            commonUser.sendMessage("User not found");
            return;
        }
        EconomyMessage economyMessage = EconomyMessage.pay(commonUser.getName(), username, amount);
        new ResponseAwaiter(economyMessage) {
            @Override
            public void onReceive(EconomyResponse economyResponse) {
                if (economyResponse.isSuccess()) {
                    commonUser.sendMessage(miniMessage.deserialize(sendFormat.replace("%amount%", String.valueOf(amount)).replace("%player%", username)));
                    target.sendMessage(miniMessage.deserialize(sendFormatOther.replace("%amount%", String.valueOf(amount)).replace("%player%", commonUser.getName())));
                } else
                    commonUser.sendMessage("Failed to send " + amount + " to " + username + ". Reason: " + economyResponse.getMessage());
            }
        };
    }
}
