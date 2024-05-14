package com.rainsoul.bilibili.dao;

import com.rainsoul.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {
    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet);
}
