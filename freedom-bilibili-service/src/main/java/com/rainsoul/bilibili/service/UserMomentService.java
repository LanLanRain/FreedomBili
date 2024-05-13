package com.rainsoul.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.rainsoul.bilibili.dao.UserMomentsDao;
import com.rainsoul.bilibili.domain.UserMoment;
import com.rainsoul.bilibili.domain.constant.UserMomentsConstant;
import com.rainsoul.bilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class UserMomentService {

    @Autowired
    private UserMomentsDao userMomentsDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 添加用户动态信息。
     *
     * @param userMoment 包含用户动态详情的实体对象。
     * @throws Exception 如果添加过程出现异常，则抛出Exception。
     */
    public void addUserMoments(UserMoment userMoment) throws Exception {
        // 为用户动态设置创建时间
        userMoment.setCreateTime(new Date());
        // 将用户动态信息添加到数据库
        userMomentsDao.addUserMoments(userMoment);

        // 从应用上下文中获取RocketMQ的生产者实例
        DefaultMQProducer momentsProducer = (DefaultMQProducer) applicationContext
                .getBean("momentsProducer");
        // 将用户动态信息转换为JSON字符串，封装成RocketMQ的消息对象
        Message message = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject
                .toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        // 向RocketMQ发送消息
        RocketMQUtil.syncSendMsg(momentsProducer, message);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        String key = "subscribed-" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONObject.parseArray(listStr, UserMoment.class);
    }
}
