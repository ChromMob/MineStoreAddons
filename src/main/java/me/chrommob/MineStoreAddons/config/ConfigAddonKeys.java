package me.chrommob.MineStoreAddons.config;

import me.chrommob.minestore.common.config.Configuration;

public enum ConfigAddonKeys {
    PURCHASE_ANNOUNCER_ENABLED(new Configuration("purchase-announcer.enabled", true)),
    PURCHASE_ANNOUNCER_FORMAT(new Configuration("purchase-announcer.format", "<red><bold>%player%</bold></red> bought <bold>%package%</bold> for <gold>%price%")),

    USER_INFO_ENABLED(new Configuration("user-info.enabled", true)),
    USER_INFO_NOT_FOUND(new Configuration("user-info.not-found", "<red>%player% did not buy any packages yet.")),
    USER_INFO_GUI_TITLE(new Configuration("user-info.gui.title", "<gold>%player%'s packages")),
    USER_INFO_GUI_ITEM(new Configuration("user-info.gui.item", "PAPER")),

    ECONOMY_ENABLED(new Configuration("economy.enabled", true)),
    ECONOMY_BALANCE_FORMAT(new Configuration("economy.message.balance", "<gold>Your balance is %amount%")),
    ECONOMY_ADD_FORMAT_SELF(new Configuration("economy.message.add", "<gold>Added %amount% to your balance")),
    ECONOMY_ADD_FORMAT_OTHER(new Configuration("economy.message.add-other", "<gold>Added %amount% to %player%'s balance")),
    ECONOMY_REMOVE_FORMAT_SELF(new Configuration("economy.message.remove", "<gold>Removed %amount% from your balance")),
    ECONOMY_REMOVE_FORMAT_OTHER(new Configuration("economy.message.remove-other", "<gold>Removed %amount% from %player%'s balance")),
    ECONOMY_SET_FORMAT_SELF(new Configuration("economy.message.set", "<gold>Set your balance to %amount%")),
    ECONOMY_SET_FORMAT_OTHER(new Configuration("economy.message.set-other", "<gold>Set %player%'s balance to %amount%")),
    ECONOMY_SEND_FORMAT_SELF(new Configuration("economy.message.send", "<gold>Sent %amount% to %player%")),
    ECONOMY_SEND_FORMAT_OTHER(new Configuration("economy.message.send-other", "<gold>Received %amount% from %player%")),

    MANUAL_REDEEM_ENABLED(new Configuration("manual-redeem.enabled", true)),
    MANUAL_REDEEM_MESSAGE_ENABLED(new Configuration("manual-redeem.message-enabled", true)),
    MANUAL_REDEEM_MESSAGE_FORMAT(new Configuration("manual-redeem.message", "<gold>You have %amount% packages to redeem. Run <bold>/redeem</bold> to redeem them.")),
    MANUAL_REDEEM_NO_PACKAGES(new Configuration("manual-redeem.no-packages", "<gold>You have no packages to redeem.")),
    MANUAL_REDEEM_GUI_TITLE(new Configuration("manual-redeem.gui.title", "<gold>Redeem packages")),
    MANUAL_REDEEM_GUI_ITEM(new Configuration("manual-redeem.gui.item", "PAPER"));

    private final Configuration configuration;

    ConfigAddonKeys(final Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
