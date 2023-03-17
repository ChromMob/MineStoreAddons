package me.chrommob.MineStoreAddons.features.userInfo;

import com.google.gson.Gson;
import me.chrommob.MineStoreAddons.MineStoreAddonsMain;
import me.chrommob.MineStoreAddons.socket.SocketResponse;
import me.chrommob.minestore.common.MineStoreCommon;

public class UserInfo implements SocketResponse {
    private final MineStoreAddonsMain main;
    private final Gson gs = new Gson();
    public UserInfo(MineStoreAddonsMain main) {
        this.main = main;
        init();
    }

    private void init() {
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
        MineStoreCommon.getInstance().log("Received userinfo response: " + split[1]);
        InfoResponse infoResponse = gs.fromJson(split[1], InfoResponse.class);
        if (infoResponse == null) {
            MineStoreCommon.getInstance().log("Error while parsing userinfo response");
            return;
        }
        main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(infoResponse.getUsername() + "paid " + infoResponse.getAmount() + " for:");
        infoResponse.getPackageAmount().forEach((packageName, amount) -> {
            main.getCommon().userGetter().get(infoResponse.getSender()).sendMessage(packageName + " x" + amount + " for " + infoResponse.getPackagePrice().get(packageName));
        });
    }
}
