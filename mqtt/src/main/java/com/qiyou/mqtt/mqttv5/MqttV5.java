package com.qiyou.mqtt.mqttv5;


import static com.qiyou.mqtt.mqttv5.MacSignature.macSignature;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

import org.eclipse.paho.mqttv5.client.DisconnectedBufferOptions;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttActionListener;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

/**
 * mqttv5 封装类
 * Created by puy on 2018/12/4 14:30
 */
public class MqttV5 {
    //尽力而为。消息发送者会想尽办法发送消息，但是遇到意外并不会重试。
    public static final int QOS_UNRELIABLE = 0;

    //至少一次。消息接收者如果没有知会或者知会本身丢失，消息发送者会再次发送以保证消息接收者至少会收到一次，当然可能造成重复消息。
    public static final int QOS_REPEAT = 1;

    //恰好一次。保证这种语义肯待会减少并发或者增加延时，不过丢失或者重复消息是不可接受的时候，级别2是最合适的。
    public static final int QOS_JUST = 2;

    //mqtt设置的参数实例
    private final MqttConnectionOptions mMqttConnectOptions;

    //mqtt实例
    private MqttAsyncClient mqttClient;
    private MqttProperties mMqttProperties;

    private MqttV5(Builder builder) {
        this.isAlibabaCloud = builder.isAlibabaCloud;
        this.sessionExpiryInterval = builder.sessionExpiryInterval;
        this.userName = builder.userName;
        this.password = builder.password;
        this.publishTopic = builder.publishTopic;
        this.subscribeTopic = builder.subscribeTopic;
        this.subscribeTopics = builder.subscribeTopics;
        this.subscribeQos = builder.subscribeQos;
        this.publishQos = builder.publishQos;
        this.clientId = builder.clientId;
        this.cleanStart = builder.cleanStart;
        this.maxReconnectDelay = builder.maxReconnectDelay;
        this.automaticReconnect = builder.automaticReconnect;
        this.connectionTimeout = builder.connectionTimeout;
        this.keepAliveInterval = builder.keepAliveInterval;
        this.publishMsgRetained = builder.publishMsgRetained;
        this.userContext = builder.userContext;
        this.socketFactory = builder.socketFactory;
        this.mIReceiveActionListener = builder.mIReceiveActionListener;
        this.mIMqttStatusListener = builder.mIMqttStatusListener;
        mMqttConnectOptions = new MqttConnectionOptions();
        mMqttConnectOptions.setAutomaticReconnect(this.automaticReconnect);
        mMqttConnectOptions.setMaxReconnectDelay(this.maxReconnectDelay);
        mMqttConnectOptions.setUserName(this.userName);
        mMqttConnectOptions.setCleanStart(this.cleanStart);
        mMqttConnectOptions.setPassword(this.password.getBytes());
        mMqttConnectOptions.setKeepAliveInterval(this.keepAliveInterval);
        mMqttConnectOptions.setConnectionTimeout(this.connectionTimeout);
        mMqttConnectOptions.setSessionExpiryInterval(this.sessionExpiryInterval);
        mMqttConnectOptions.setHttpsHostnameVerificationEnabled(this.httpsHostnameVerificationEnabled);
        if (sslHostnameVerifier != null) {
            mMqttConnectOptions.setSSLHostnameVerifier(this.sslHostnameVerifier);
        }
        if (this.socketFactory != null) {
            mMqttConnectOptions.setSocketFactory(this.socketFactory);
        }
        mMqttProperties = new MqttProperties();
        mMqttProperties.setSubscriptionIdentifiers(Arrays.asList(1, 2, 3, 4));
    }

    /**
     * 进行链接
     *
     * @param serverURI 链接的地址
     */
    public void connect(String serverURI) {

        try {
            mqttClient = new MqttAsyncClient(serverURI, this.clientId, new MemoryPersistence());
            //设置回调
            mqttClient.setCallback(new MqttCallback() {

                @Override
                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    //连接丢失后
                    if (mIMqttStatusListener != null) {
                        mIMqttStatusListener.connectLost(disconnectResponse.getException());
                    }
                }

                @Override
                public void mqttErrorOccurred(MqttException exception) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttToken token) {

                }

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (mIMqttStatusListener != null) {
                        mIMqttStatusListener.connectComplete(reconnect, serverURI);
                    }
                }

                @Override
                public void authPacketArrived(int reasonCode, MqttProperties properties) {

                }
            });
            mqttClient.connect(mMqttConnectOptions, this.userContext, new MqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //System.out.println("链接成功");
                    if (mIMqttStatusListener != null) {
                        mIMqttStatusListener.connectSuccess(asyncActionToken);
                    }
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttClient.setBufferOpts(disconnectedBufferOptions);
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //  System.out.println("链接失败");
                    if (mIMqttStatusListener != null) {
                        mIMqttStatusListener.connectFail(asyncActionToken, exception);
                    }
                }
            });
        } catch (MqttException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅
     */
    private void subscribe() {
        try {
            if (MqttV5.this.subscribeTopic != null) { //订阅单个Topic
                mqttClient.subscribe(new MqttSubscription(MqttV5.this.subscribeTopic, this.subscribeQos), this.userContext,
                        iMqttActionListener, iMqttMessageListener, mMqttProperties);
            } else {//订阅多个Topic
                if (MqttV5.this.subscribeTopics != null && MqttV5.this.subscribeTopics.size() > 0) {
                    int size = MqttV5.this.subscribeTopics.size();
                    MqttSubscription[] subscribeTopics = new MqttSubscription[size];
                    IMqttMessageListener[] messageListeners = new IMqttMessageListener[size];
                    for (int i = 0; i < size; i++) {
                        subscribeTopics[i] = new MqttSubscription(MqttV5.this.subscribeTopics.get(i), QOS_UNRELIABLE);
                        messageListeners[i] = iMqttMessageListener;
                    }
                    mqttClient.subscribe(subscribeTopics, this.userContext, iMqttActionListener, messageListeners, mMqttProperties);
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅消息
     *
     * @param topic
     */
    public void subscribe(String topic, MqttProperties mqttProperties) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            IMqttMessageListener[] messageListeners = new IMqttMessageListener[1];
            messageListeners[0] = iMqttMessageListener;
            mqttClient.subscribe(new MqttSubscription(topic, this.subscribeQos), this.userContext,
                    iMqttActionListener, iMqttMessageListener, mqttProperties == null ? mMqttProperties : mqttProperties);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅消息
     *
     * @param topic        topic
     * @param subscribeQos 类型
     */
    public void subscribe(String topic, int subscribeQos, MqttProperties mqttProperties) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            mqttClient.subscribe(new MqttSubscription(topic, subscribeQos), this.userContext,
                    iMqttActionListener, iMqttMessageListener, mqttProperties == null ? mMqttProperties : mqttProperties);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅消息
     *
     * @param topics 多个topic
     */
    public void subscribe(List<String> topics, MqttProperties mqttProperties) {
        if (topics == null || topics.size() <= 0) {
            return;
        }
        try {
            int size = topics.size();
            MqttSubscription[] subscribeTopics = new MqttSubscription[size];
            IMqttMessageListener[] messageListeners = new IMqttMessageListener[size];
            for (int i = 0; i < size; i++) {
                subscribeTopics[i] = new MqttSubscription(topics.get(i), QOS_UNRELIABLE);
                messageListeners[i] = iMqttMessageListener;
            }
            mqttClient.subscribe(subscribeTopics, this.userContext, iMqttActionListener, messageListeners, mqttProperties == null ? mMqttProperties : mqttProperties);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅消息
     *
     * @param topics       多个topic
     * @param subscribeQos 类型
     */
    public void subscribe(List<String> topics, int subscribeQos, MqttProperties mqttProperties) {
        if (topics == null || topics.size() <= 0) {
            return;
        }
        try {
            int size = topics.size();
            MqttSubscription[] subscribeTopics = new MqttSubscription[size];
            IMqttMessageListener[] messageListeners = new IMqttMessageListener[size];
            for (int i = 0; i < size; i++) {
                subscribeTopics[i] = new MqttSubscription(topics.get(i), subscribeQos);
                messageListeners[i] = iMqttMessageListener;
            }
            mqttClient.subscribe(subscribeTopics, this.userContext, iMqttActionListener, messageListeners, mqttProperties == null ? mMqttProperties : mqttProperties);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe(String topic) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            mqttClient.unsubscribe(topic, this.userContext, iMqttUnActionListener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSubscribe(List<String> topics) {
        if (topics == null || topics.size() <= 0) {
            return;
        }
        try {
            int size = topics.size();
            String[] subscribeTopics = topics.toArray(new String[size]);
            mqttClient.unsubscribe(subscribeTopics, this.userContext, iMqttUnActionListener, null);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅后的消息回调--真正的接收消息
     */
    private IMqttMessageListener iMqttMessageListener = new IMqttMessageListener() {


        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            if (MqttV5.this.mIReceiveActionListener != null) {
                MqttV5.this.mIReceiveActionListener.receiveMsg(topic, mqttMessage.toString());
            }
        }
    };

    /**
     * 订阅的动作后的回调
     */
    private MqttActionListener iMqttActionListener = new MqttActionListener() {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            //System.out.println("订阅成功");
            if (mIMqttStatusListener != null) {
                mIMqttStatusListener.subscribeSuccess(iMqttToken);
            }
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            //System.out.println("订阅失败");
            if (mIMqttStatusListener != null) {
                mIMqttStatusListener.subscribeFail(iMqttToken, throwable);
            }
        }
    };

    /**
     * 订阅的动作后的回调
     */
    private MqttActionListener iMqttUnActionListener = new MqttActionListener() {
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            //System.out.println("订阅成功");
            if (mIMqttStatusListener != null) {
                mIMqttStatusListener.unSubscribeSuccess(iMqttToken);
            }
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            //System.out.println("订阅失败");
            if (mIMqttStatusListener != null) {
                mIMqttStatusListener.unSubscribeFail(iMqttToken, throwable);
            }
        }
    };

    /**
     * 发布消息（默认topic）
     *
     * @param msg 消息
     */
    public void publishMsg(String msg, IPublishActionListener iPublishActionListener) {
        try {
            //retained:设置发送完消息后是否还保留 与cleanSession差不多
            if (mqttClient.isConnected()) {
                mqttClient.publish(publishTopic, msg.getBytes(), this.publishQos, this.publishMsgRetained, this.userContext, iPublishActionListener);
            } else {
                iPublishActionListener.onConnectionLost();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param topic                  topic
     * @param msg                    发送的内容
     * @param iPublishActionListener 回调
     */
    public void publishMsg(String topic, String msg, IPublishActionListener iPublishActionListener) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            //retained:设置发送完消息后是否还保留 与cleanSession差不多
            if (mqttClient.isConnected()) {
                mqttClient.publish(topic, msg.getBytes(), this.publishQos, this.publishMsgRetained, this.userContext, iPublishActionListener);
            } else {
                iPublishActionListener.onConnectionLost();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param topic                  topic
     * @param qos                    发送类型
     * @param retained               是在服务器否保存消息
     * @param msg                    发送的内容
     * @param iPublishActionListener 回调
     */
    public void publishMsg(String topic, int qos, boolean retained, String msg, IPublishActionListener iPublishActionListener) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            //retained:设置发送完消息后是否还保留 与cleanSession差不多
            if (mqttClient.isConnected()) {
                mqttClient.publish(topic, msg.getBytes(), qos, retained, this.userContext, iPublishActionListener);
            } else {
                iPublishActionListener.onConnectionLost();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param topic                  topic
     * @param qos                    发送类型
     * @param retained               是在服务器否保存消息
     * @param msg                    发送的内容
     * @param iPublishActionListener 回调
     */
    public void publishMsg(String topic, int qos, boolean retained, byte[] msg, IPublishActionListener iPublishActionListener) {
        try {
            if (TextUtils.isEmpty(topic)) return;
            //retained:设置发送完消息后是否还保留 与cleanSession差不多
            if (mqttClient.isConnected()) {
                mqttClient.publish(topic, msg, qos, retained, this.userContext, iPublishActionListener);
            } else {
                iPublishActionListener.onConnectionLost();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private Long sessionExpiryInterval;
    private boolean isAlibabaCloud = false;
    private String clientId;
    private boolean cleanStart;
    private String publishTopic;
    private int subscribeQos;
    private String subscribeTopic;
    private List<String> subscribeTopics;
    private int publishQos;
    private String userName;
    private String password;
    private int connectionTimeout;
    private int keepAliveInterval;
    private int maxReconnectDelay;
    private boolean automaticReconnect;
    private boolean publishMsgRetained;
    private Object userContext = null;
    private SocketFactory socketFactory;
    private boolean httpsHostnameVerificationEnabled = true;
    private HostnameVerifier sslHostnameVerifier = null;
    private IReceiveActionListener mIReceiveActionListener;
    private IMqttStatusListener mIMqttStatusListener;

    public String getClientId() {
        return clientId;
    }

    public int getSubscribeQos() {
        return subscribeQos;
    }

    public String getSubscribeTopic() {
        return subscribeTopic;
    }

    public int getPublishQos() {
        return publishQos;
    }

    public String getPublishTopic() {
        return publishTopic;
    }

    public boolean isPublishMsgRetained() {
        return publishMsgRetained;
    }

    public Object getUserContext() {
        return userContext;
    }

    public List<String> getSubscribeTopics() {
        return subscribeTopics;
    }

    public MqttConnectionOptions getmMqttConnectOptions() {
        return mMqttConnectOptions;
    }

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    public MqttAsyncClient getMqttClient() {
        return mqttClient;
    }

    public Long getSessionExpiryInterval() {
        return sessionExpiryInterval;
    }

    public static class Builder {
        private boolean isAlibabaCloud = false;
        private Long sessionExpiryInterval;
        private String publishTopic;
        private String subscribeTopic;
        private List<String> subscribeTopics;
        private int publishQos = QOS_UNRELIABLE;
        private int subscribeQos = QOS_UNRELIABLE;
        private String userName = "admin";
        private String password = "admin";
        private String clientId = "";
        private boolean cleanStart = false;
        private int connectionTimeout = 10;
        private int keepAliveInterval = 20;
        private boolean automaticReconnect = true;
        private int maxReconnectDelay = 128000;
        private boolean publishMsgRetained = true;
        private Object userContext = null;
        private SocketFactory socketFactory;
        private boolean httpsHostnameVerificationEnabled = true;
        private HostnameVerifier sslHostnameVerifier = null;
        private IReceiveActionListener mIReceiveActionListener;
        private IMqttStatusListener mIMqttStatusListener;


        public Builder setHttpsHostnameVerificationEnabled(boolean httpsHostnameVerificationEnabled) {
            this.httpsHostnameVerificationEnabled = httpsHostnameVerificationEnabled;
            return this;
        }

        public Builder setSslHostnameVerifier(HostnameVerifier sslHostnameVerifier) {
            this.sslHostnameVerifier = sslHostnameVerifier;
            return this;
        }

        /**
         * @param sessionExpiryInterval 会话过期间隔
         */
        public Builder setSessionExpiryInterval(Long sessionExpiryInterval) {
            this.sessionExpiryInterval = sessionExpiryInterval;
            return this;
        }

        /**
         * @param isAlibabaCloud 是否是阿里云mqtt
         */
        public Builder setAlibabaCloud(boolean isAlibabaCloud) {
            this.isAlibabaCloud = isAlibabaCloud;
            return this;
        }

        /**
         * @param cleanStart 会话
         */
        public Builder setCleanSession(boolean cleanStart) {
            this.cleanStart = cleanStart;
            return this;
        }

        /**
         * @param maxReconnectDelay 是否是阿里云mqtt
         */
        public Builder setMaxReconnectDelay(int maxReconnectDelay) {
            this.maxReconnectDelay = maxReconnectDelay;
            return this;
        }

        /**
         * @param automaticReconnect 设置自动重连：当断开链接时候
         */
        public Builder setAutomaticReconnect(boolean automaticReconnect) {
            this.automaticReconnect = automaticReconnect;
            return this;
        }

        /**
         * @param publishMsgRetained 推送消息后是否保留
         */
        public Builder setPublishMsgRetained(boolean publishMsgRetained) {
            this.publishMsgRetained = publishMsgRetained;
            return this;
        }

        /**
         * @param publishTopic 推送消息的topic
         */
        public Builder setPublishTopic(String publishTopic) {
            this.publishTopic = publishTopic;
            return this;
        }

        /**
         * @param subscribeTopic 订阅消息的topic
         */
        public Builder setSubscribeTopic(String subscribeTopic) {
            this.subscribeTopic = subscribeTopic;
            return this;
        }

        /**
         * @param subscribeTopics 订阅多个不同发送消息的topic
         */
        public Builder setSubscribeTopics(List<String> subscribeTopics) {
            this.subscribeTopics = subscribeTopics;
            return this;
        }

        /**
         * @param publishQos 发布的qos
         */
        public Builder setPublishQos(int publishQos) {
            this.publishQos = publishQos;
            return this;
        }

        /**
         * @param subscribeQos 设置订阅的qos
         * @return
         */
        public Builder setSubscribeQos(int subscribeQos) {
            this.subscribeQos = subscribeQos;
            return this;
        }

        /**
         * @param acessKey 设置用户名
         */
        public Builder setUserName(String acessKey) {
            this.userName = acessKey;
            return this;
        }

        /**
         * @param secretKey 设置密码
         */
        public Builder setPassword(String secretKey) {
            try {
                if (this.isAlibabaCloud) {
                    this.password = macSignature(clientId.split("@@@")[0], secretKey);
                } else {
                    this.password = secretKey;
                }
                System.out.println(this.password);
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return this;
        }

        /**
         * 设置客户端id 唯一
         *
         * @param clientId 客户端唯一id
         * @return
         */
        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * @param connectionTimeout 链接超时的时间
         */
        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * @param keepAliveInterval 保持长链接的时间
         */
        public Builder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        /**
         * @param userContext 用户上下文 :null
         */
        public Builder setUserContext(Object userContext) {
            this.userContext = userContext;
            return this;
        }

        /**
         * 设置ssl的SocketFactory
         *
         * @param socketFactory sslContext.getSocketFactory()
         */
        public Builder setSocketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        /**
         * 设置接收消息的接口
         *
         * @param IReceiveActionListener
         * @return
         */
        public Builder setIReceiveActionListener(IReceiveActionListener IReceiveActionListener) {
            mIReceiveActionListener = IReceiveActionListener;
            return this;
        }

        public Builder setIMqttStatusListener(IMqttStatusListener IMqttStatusListener) {
            mIMqttStatusListener = IMqttStatusListener;
            return this;
        }

        public MqttV5 build() {
            return new MqttV5(this);
        }
    }

    /**
     * 断开链接
     */
    public void destory(boolean force) {
        try {
            if (mqttClient != null) {
                mqttClient.close(force);
                mIMqttStatusListener = null;
                mIReceiveActionListener = null;
                mqttClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
