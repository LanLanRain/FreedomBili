package com.rainsoul.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.rainsoul.bilibili.domain.User;
import com.rainsoul.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserDao {
    User getUserByPhone(String phone);

    Integer addUser(User user);

    void addUserInfo(UserInfo userInfo);

    User getUserById(Long userId);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUserInfo(UserInfo userInfo);


    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    Integer pageCountUserInfos(Map<String, Object> params);

    List<UserInfo> pageListUserInfos(JSONObject params);
}
