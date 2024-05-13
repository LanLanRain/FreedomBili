package com.rainsoul.bilibili.dao;

import com.rainsoul.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserFollowingDao {
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    void addUserFollowing(UserFollowing userFollowing);
}
