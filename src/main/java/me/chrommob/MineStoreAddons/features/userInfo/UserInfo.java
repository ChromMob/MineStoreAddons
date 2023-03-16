package me.chrommob.MineStoreAddons.features.userInfo;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.SocketResponse;

public class UserInfo implements SocketResponse {
    private final MineStoreAddonsMain main;
    private final Gson gs = new Gson();
    public UserInfo(MineStoreAddonsMain main) {
        this.main = main;
        init();
    }

    private void init() {
        main.getCommon().command().registerCommand(new Command(this));
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
        InfoResponse infoResponse = gs.fromJson(split[1], InfoResponse.class);
        if (infoResponse == null) {
            return;
        }
        main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(infoResponse.getUsername() + "paid " + infoResponse.getAmount() + " for:");
        infoResponse.getPackageAmount().forEach((packageName, amount) -> {
            main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(packageName + " x" + amount + " for " + infoResponse.getPackagePrice().get(packageName));
        });
    }
}
