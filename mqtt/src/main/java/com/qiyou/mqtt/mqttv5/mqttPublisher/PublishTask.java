package com.qiyou.mqtt.mqttv5.mqttPublisher;/*
 *       .__
 *  _____|__|___.__. ____  __ __
 * / ____/  <   |  |/  _ \|  |  \
 *< <_|  |  |\___  (  <_> )  |  /
 * \__   |__|/ ____|\____/|____/
 *    |__|   \/
 *
 * create by puy at 2024/11/11
 * des:
 */

import com.qiyou.mqtt.mqttv5.IPublishActionListener;

public class PublishTask {
    String topic;
    byte[] payload;
    int qos;
    boolean retained;
    Object userContext;
    IPublishActionListener listener;

    PublishTask(String topic, byte[] msg, int qos, boolean retained, Object userContext, IPublishActionListener listener) {
        this.topic = topic;
        this.qos = qos;
        this.retained = retained;
        this.payload = msg;
        this.userContext = userContext;
        this.listener = listener;
    }
}
