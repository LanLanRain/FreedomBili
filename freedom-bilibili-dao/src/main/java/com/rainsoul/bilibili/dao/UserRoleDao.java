package com.rainsoul.bilibili.dao;

import com.rainsoul.bilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {
    List<UserRole> getUserRoleByUserId(Long userId);
}
