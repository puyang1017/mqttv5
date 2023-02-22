//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qiyou.mqtt.mqttv5.org.apache.commons.codec.language;


import com.qiyou.mqtt.mqttv5.org.apache.commons.codec.base.StringEncoder;
import com.qiyou.mqtt.mqttv5.org.apache.commons.codec.base.EncoderException;

public abstract class AbstractCaverphone implements StringEncoder {
    public AbstractCaverphone() {
    }

    public Object encode(Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
        } else {
            return this.encode((String)source);
        }
    }

    public boolean isEncodeEqual(String str1, String str2) throws EncoderException {
        return this.encode(str1).equals(this.encode(str2));
    }
}
