package com.rainsoul.bilibili.api.support;

import com.rainsoul.bilibili.domain.exception.ConditionException;
import com.rainsoul.bilibili.service.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {
    /**
     * 获取当前用户的ID。
     * 该方法首先从请求头中提取token，然后通过token验证获取用户ID。
     * 如果token验证失败，将抛出一个条件异常。
     *
     * @return Long 当前用户的ID。
     * @throws ConditionException 如果token验证失败，抛出此异常。
     */
    public Long getCurrentUserId() {
        // 获取当前请求的属性
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 从请求头中获取token
        String token = requestAttributes.getRequest().getHeader("token");
        // 验证token并获取用户ID
        Long userId = TokenUtil.verifyToken(token);
        // 如果用户ID小于0，表示token验证失败，抛出异常
        if (userId < 0) {
            throw new ConditionException("非法用户");
        }
        return userId;
    }
}
