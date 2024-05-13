package com.rainsoul.bilibili.service.config;

import com.alibaba.fastjson.JSONObject;
import com.rainsoul.bilibili.domain.UserFollowing;
import com.rainsoul.bilibili.domain.UserMoment;
import com.rainsoul.bilibili.domain.constant.UserMomentsConstant;
import com.rainsoul.bilibili.service.UserFollowingService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RocketMQConfig {
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 创建并配置一个名为“momentsProducer”的DefaultMQProducer实例。
     * 这个方法不接受任何参数，它会初始化一个Producer对象，设置NameServer地址，并启动Producer。
     *
     * @return DefaultMQProducer 返回配置好的DefaultMQProducer实例。
     * @throws Exception 如果在创建或配置过程中发生错误，则会抛出异常。
     */
    @Bean(name = "momentsProducer")
    public DefaultMQProducer getMomentsProducer() throws Exception {
        // 创建一个DefaultMQProducer实例，并设置其组名为USER_MOMENTS的常量。
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        // 设置NameServer的地址。
        producer.setNamesrvAddr(nameServerAddr);
        // 启动Producer。
        producer.start();
        return producer;
    }


    /**
     * 创建并配置一个用于消费消息的DefaultMQPushConsumer实例。
     * 这个消费者属于“时刻”消息组，订阅了“时刻”主题下的所有消息。
     *
     * @return 返回配置好的DefaultMQPushConsumer实例，可以被其他部分引用以进行消息消费。
     * @throws Exception 如果在配置或启动消费者时发生错误，则抛出异常。
     */
    @Bean(name = "momentsConsumer")
    public DefaultMQPushConsumer getMomentsConsumer() throws Exception {
        // 初始化并配置DefaultMQPushConsumer实例，设置消费者组ID
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr); // 设置NameServer地址
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*"); // 订阅“时刻”主题下的所有消息

        // 注册消息监听器，用于处理接收到的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                // 处理消息逻辑
                MessageExt messageExt = list.get(0);
                if (messageExt == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(messageExt.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                Long userId = userMoment.getUserId();
                List<UserFollowing> userFansList = userFollowingService.getUserFans(userId); // 获取用户粉丝列表

                // 更新每个粉丝的订阅列表
                for (UserFollowing userFans : userFansList) {
                    String key = "subscribed-" + userFans.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoment> subscribedList;
                    if (StringUtil.isNullOrEmpty(subscribedListStr)) {
                        subscribedList = new ArrayList<>();
                    } else {
                        subscribedList = JSONObject.parseArray(subscribedListStr, UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start(); // 启动消费者
        return consumer;
    }


}
