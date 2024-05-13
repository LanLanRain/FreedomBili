package com.rainsoul.bilibili.dao;

import com.rainsoul.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowingGroupDao {
    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);
}
