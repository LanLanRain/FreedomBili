package com.rainsoul.bilibili.api.aspect;

import com.rainsoul.bilibili.api.support.UserSupport;
import com.rainsoul.bilibili.domain.annotation.ApiLimitedRole;
import com.rainsoul.bilibili.domain.auth.UserRole;
import com.rainsoul.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(1)
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.rainsoul.bilibili.domain.annotation.ApiLimitedRole)")
    public void check() {

    }

    /**
     * 在目标方法执行前执行的逻辑，用于检查当前用户是否有权限访问被注解为@apiLimitedRole的方法。
     *
     * @param joinPoint 切面连接点，表示当前被拦截的方法。
     * @param apiLimitedRole 被注解的方法所携带的ApiLimitedRole注解实例，用于获取限制访问的角色列表。
     * @throws Exception 如果当前用户角色不在允许访问的角色列表内，则抛出异常。
     */
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole) throws Exception {
        // 获取当前用户ID
        Long userId = userSupport.getCurrentUserId();
        // 根据用户ID获取用户角色列表
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 从注解中获取限制访问的角色代码列表
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        // 将角色代码列表转换为集合
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        // 获取当前用户拥有的角色代码集合
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        // 保留当前用户拥有的且在限制访问列表中的角色代码
        roleCodeSet.retainAll(limitedRoleCodeSet);
        // 如果存在交集（即用户拥有限制访问的角色），则抛出异常
        if (!roleCodeSet.isEmpty()) {
            throw new Exception("访问接口被限制");
        }
    }
}
