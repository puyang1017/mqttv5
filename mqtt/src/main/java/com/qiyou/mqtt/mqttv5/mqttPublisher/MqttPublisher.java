package com.qiyou.mqtt.mqttv5.mqttPublisher;/*
 *       .__
 *  _____|__|___.__. ____  __ __
 * / ____/  <   |  |/  _ \|  |  \
 *< <_|  |  |\___  (  <_> )  |  /
 * \__   |__|/ ____|\____/|____/
 *    |__|   \/
 *
 * create by puy at 2024/11/11
 * des:mqtt 发布队列消息
 */

import android.text.TextUtils;

import com.qiyou.mqtt.mqttv5.IPublishActionListener;

import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.reflect.Proxy;


public class MqttPublisher {
    private final MqttAsyncClient mqttClient;
    private final ExecutorService executorService;
    private final BlockingQueue<PublishTask> taskQueue;
    private boolean starTask = false;

    public MqttPublisher(MqttAsyncClient client) {
        this.mqttClient = client;
        this.executorService = Executors.newSingleThreadExecutor(); // 确保任务按顺序执行
        this.taskQueue = new LinkedBlockingQueue<>();
    }


    // 把任务放入队列中
    public void publishMsg(String topic, byte[] msg, int qos, boolean retained, Object userContext, boolean isParallel, IPublishActionListener listener) {
        if (TextUtils.isEmpty(topic)) {
            return;
        }
        if (isParallel) {//并行发送
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.publish(topic, msg, qos, retained, userContext, listener);
                } else {
                    listener.onConnectionLost();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//队列发送
            taskQueue.offer(new PublishTask(topic, msg, qos, retained, userContext, listener));
            if (!starTask) {
                handleNextTask();
            }
        }
    }

    private void executeTask(PublishTask task) {
        if (!mqttClient.isConnected()) {
            task.listener.onConnectionLost();
            handleNextTask();
            return;
        }

        try {
            IPublishActionListener proxyListener = createProxy(task.listener);
//            System.out.println("MqttPublisher mqtt 发送执行 " + task.topic);
            mqttClient.publish(task.topic, task.payload, task.qos, task.retained, task.userContext, proxyListener);
        } catch (MqttException e) {
            handleNextTask();
        }
    }

    private IPublishActionListener createProxy(IPublishActionListener realListener) {
        return (IPublishActionListener) Proxy.newProxyInstance(
                realListener.getClass().getClassLoader(),
                new Class[]{IPublishActionListener.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 调用实际的方法
                        Object result = method.invoke(realListener, args);

                        // 在onSuccess方法被调用后触发下一个任务
                        if ("onSuccess".equals(method.getName())) {
//                            System.out.println("MqttPublisher mqtt 发送成功执行下一条");
                            handleNextTask(); // 启动下一个任务
                        }
                        // 在onFailure方法被调用后也可以选择触发下一个任务
                        else if ("onFailure".equals(method.getName())) {
//                            System.out.println("MqttPublisher mqtt 发送失败执行下一条");
                            handleNextTask();
                        }

                        // 在onConnectionLost方法被调用后也可以选择触发下一个任务
                        else if ("onConnectionLost".equals(method.getName())) {
//                            System.out.println("MqttPublisher mqtt 发送失败执行下一条");
                            handleNextTask();
                        }

                        return result;
                    }
                }
        );
    }

    private void handleNextTask() {
        starTask = true;
        if (!taskQueue.isEmpty()) {
            PublishTask nextTask = taskQueue.poll();
            if (nextTask != null) {
                executeTask(nextTask);
            } else {
                starTask = false;
            }
        } else {
            starTask = false;
        }
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
