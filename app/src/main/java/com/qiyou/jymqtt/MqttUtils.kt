package com.qiyou.jymqtt

import android.content.Context
import android.util.Log
import com.qiyou.mqtt.mqttv5.IMqttStatusListener
import com.qiyou.mqtt.mqttv5.MqttConfig
import com.qiyou.mqtt.mqttv5.MqttV5
import org.eclipse.paho.mqttv5.client.IMqttToken

object MqttUtils {
    var mqttV5: MqttV5? = null
    val TAG = "MqttUtils"

    /**
     * 连接mqtt
     */
    fun connect(context: Context) {
        MqttConfig.mqttClientId = "puyang"
        MqttConfig.mqttUserName = "ljbclient01"
        MqttConfig.mqttUserPw = "Z6bsl+eVuAKwbe5c7VRy+S"


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
            //.setSocketFactory(SSLUtil.getSSL())
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
        mqttV5?.connect("tcp://139.224.44.126:1883")
    }

    fun disConnect(){
        mqttV5?.destory()
        mqttV5 = null
    }
}