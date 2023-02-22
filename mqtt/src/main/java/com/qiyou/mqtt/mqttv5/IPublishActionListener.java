package com.qiyou.mqtt.mqttv5;

import org.eclipse.paho.mqttv5.client.MqttActionListener;

/**
 * Created by puy on 2018/12/4 15:31
 */


public interface IPublishActionListener extends MqttActionListener {

    /**
     * 链接丢失
     */
    void onConnectionLost();

}