package com.qiyou.mqtt.mqttv5;

/**
 * Created by puy on 2018/12/4 15:31
 */
public interface IReceiveActionListener {
    void receiveMsg(String topic, String msg);
}
