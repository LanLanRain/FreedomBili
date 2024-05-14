package com.rainsoul.bilibili.service;

import com.rainsoul.bilibili.domain.auth.AuthRoleElementOperation;
import com.rainsoul.bilibili.domain.auth.AuthRoleMenu;
import com.rainsoul.bilibili.domain.auth.UserAuthorities;
import com.rainsoul.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;

    /**
     * 根据用户ID获取用户权限信息。
     *
     * @param userId 用户的唯一标识符。
     * @return UserAuthorities 包含用户角色操作权限和角色菜单权限的对象。
     */
    public UserAuthorities getUserAuthorities(Long userId) {
        // 通过用户ID获取用户角色列表
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 将用户角色列表转换为角色ID的集合
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        // 根据角色ID集合获取角色的操作权限列表
        List<AuthRoleElementOperation> authRoleElementOperationList =
                authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);

        // 根据角色ID集合获取角色的菜单权限列表
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIdSet);

        // 创建并初始化UserAuthorities对象，设置角色操作权限列表和角色菜单权限列表
        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(authRoleElementOperationList);
        userAuthorities.setRoleMenuList(authRoleMenuList);
        return userAuthorities;
    }

}
