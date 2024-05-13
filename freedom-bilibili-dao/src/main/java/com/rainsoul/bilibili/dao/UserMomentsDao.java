package com.rainsoul.bilibili.dao;

import com.rainsoul.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsDao {
    void addUserMoments(UserMoment userMoment);
}
