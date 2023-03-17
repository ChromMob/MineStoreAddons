package me.chrommob.MineStoreAddons.features.userInfo;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;

import java.util.Map;

public class UserInfo implements SocketResponse {
    private final MineStoreAddonsMain main;
    private String notFound;
    private final Gson gs = new Gson();
    public UserInfo(MineStoreAddonsMain main) {
        this.main = main;
        init();
    }

    private void init() {
        Map<String, Object> userInfo = (Map<String, Object>) main.getConfig().get("user-info");
        notFound = (String) userInfo.get("not-found");
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
        main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(infoResponse.getUsername() + " paid " + infoResponse.getAmount() + " for:");
        infoResponse.getPackageAmount().forEach((packageName, amount) -> {
            main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(packageName + " x" + amount + " for " + infoResponse.getPackagePrice().get(packageName));
        });
    }
}
