package com.qiyou.jymqtt

import android.content.Context
import android.util.Log
import com.qiyou.mqtt.mqttv5.IMqttStatusListener
import com.qiyou.mqtt.mqttv5.MqttConfig
import com.qiyou.mqtt.mqttv5.MqttV5
import com.qiyou.mqtt.mqttv5.SSLUtil
import org.eclipse.paho.mqttv5.client.IMqttToken


object MqttUtils {
    var mqttV5: MqttV5? = null
    val TAG = "MqttUtils"

    /**
     * 连接mqtt
     */
    fun connect(context: Context) {
        //5.0服务器
        MqttConfig.mqttClientId = "puyang"
        MqttConfig.mqttUserName = "ljbclient01"
        MqttConfig.mqttUserPw = "Z6bsl+eVuAKwbe5c7VRy+S"
        //mqtt3.0 服务器
//        MqttConfig.mqttClientId = "GID_LJB_APP@@@17ddd893d178cb56bb14b2c2e8e2c790_1703050701"
//        MqttConfig.mqttUserName = "ljb_background"
//        MqttConfig.mqttUserPw = "6GvG89ScTNzrrm7sfC0qC"

        mqttV5 = MqttV5.Builder()
            .setUserContext(context)
            .setClientId(MqttConfig.mqttClientId)
            .setUserName(MqttConfig.mqttUserName)
            .setPassword(MqttConfig.mqttUserPw)
            .setSubscribeTopics(arrayListOf("testtopic/#"))
            .setKeepAliveInterval(10)
            .setMaxReconnectDelay(3000)
            .setSessionExpiryInterval(24*60*60)
            .setSubscribeQos(MqttV5.QOS_REPEAT)
            .setPublishQos(MqttV5.QOS_REPEAT)
            .setPublishMsgRetained(true)
            .setCleanSession(false)
            .setSocketFactory(SSLUtil.getSSL())
            .setHttpsHostnameVerificationEnabled(false)
            //.setSslHostnameVerifier { hostname, session -> true }
            .setIReceiveActionListener { topic, msg ->
                try {

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } //设置接收消息的监听
            .setIMqttStatusListener(object : IMqttStatusListener {
                override fun connectSuccess(asyncActionToken: IMqttToken) {
                    Log.d(TAG, "external receive 连接成功")
                }

                override fun connectFail(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d(TAG, "external receive 连接失败$exception")
                }

                override fun unSubscribeSuccess(iMqttToken: IMqttToken) {
                    Log.d(TAG, "external receive 取消订阅成功:$iMqttToken")
                }

                override fun unSubscribeFail(iMqttToken: IMqttToken?, throwable: Throwable?) {
                    Log.d(TAG, "external receive 取消订阅失败$throwable")
                }

                override fun subscribeSuccess(iMqttToken: IMqttToken) {
                    Log.d(TAG, "external receive 订阅成功:$iMqttToken")
                }

                override fun subscribeFail(iMqttToken: IMqttToken, throwable: Throwable) {
                    Log.d(TAG, "external receive 订阅失败$throwable")
                }


                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    Log.d(TAG, "external receive 连接完成 reconnect=$reconnect serverURI=$serverURI")
                }

                override fun connectLost(cause: Throwable) {
                    Log.d(TAG, "external receive 连接丢失$cause")
                }
            }) //设置链接状态的监听
            .setIReceiveActionListener { topic, msg ->
                try {
                    Log.d(TAG, "external receive 接收: topic:$topic msg:$msg")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .build()
        mqttV5?.connect("ssl://139.224.44.126:1883")
        //"ssl://139.224.44.126:1883" 5.0服务器
        //"ssl://47.114.203.159:8883" 3.0服务器
    }

    fun disConnect(){
        mqttV5?.destory()
        mqttV5 = null
    }
}