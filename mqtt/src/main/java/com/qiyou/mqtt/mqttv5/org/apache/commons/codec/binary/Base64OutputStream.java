//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qiyou.mqtt.mqttv5.org.apache.commons.codec.binary;

import java.io.OutputStream;

public class Base64OutputStream extends BaseNCodecOutputStream {
    public Base64OutputStream(OutputStream out) {
        this(out, true);
    }

    public Base64OutputStream(OutputStream out, boolean doEncode) {
        super(out, new Base64(false), doEncode);
    }

    public Base64OutputStream(OutputStream out, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(out, new Base64(lineLength, lineSeparator), doEncode);
    }
}
