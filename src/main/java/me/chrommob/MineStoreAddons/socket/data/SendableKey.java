package me.chrommob.MineStoreAddons.socket.data;

import java.security.Key;

public class SendableKey implements Key {
    private String algorithm;
    private String format;
    private byte[] encoded;
    public SendableKey(String algorithm, String format, byte[] encoded) {
        this.algorithm = algorithm;
        this.format = format;
        this.encoded = encoded;
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public byte[] getEncoded() {
        return encoded;
    }
}
