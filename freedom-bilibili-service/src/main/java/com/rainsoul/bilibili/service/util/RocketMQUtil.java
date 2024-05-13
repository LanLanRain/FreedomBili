package com.rainsoul.bilibili.service.util;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * RocketMQUtil 是一个用于RocketMQ消息发送的工具类。
 */
public class RocketMQUtil {

    /**
     * 同步发送消息。
     *
     * @param producer 消息生产者实例，用于发送消息。
     * @param msg 要发送的消息内容。
     * @throws MQBrokerException 当与消息队列代理通信时发生异常。
     * @throws RemotingException 当远程调用失败时抛出。
     * @throws InterruptedException 当线程被中断时抛出。
     * @throws MQClientException 当客户端发生异常时抛出。
     */
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        SendResult sendResult = producer.send(msg);
        System.out.println("sendResult = " + sendResult);
    }

    /**
     * 异步发送消息。
     *
     * @param producer 消息生产者实例，用于发送消息。
     * @param msg 要发送的消息内容。
     * @throws RemotingException 当远程调用失败时抛出。
     * @throws InterruptedException 当线程被中断时抛出。
     * @throws MQClientException 当客户端发生异常时抛出。
     */
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws RemotingException, InterruptedException, MQClientException {
        // 设置等待异步发送结果的计数器
        int messageCount = 2;
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
            // 异步发送消息并设置回调
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    // 成功发送消息，计数器减一
                    countDownLatch.countDown();
                    System.out.println("sendResult = " + sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    // 发送消息失败，计数器减一
                    countDownLatch.countDown();
                    System.out.println("发送消息的时候发生了异常! " + throwable);
                    throwable.printStackTrace();
                }
            });
        }
        // 等待所有消息发送完成或超时
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

}
