package com.rainsoul.bilibili.domain.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 用于标记方法，限制其可被访问的角色。
 * 此注解适用于需要进行角色限制的API方法上。
 *
 * @Retention(RetentionPolicy.RUNTIME) 表示该注解在运行时可被读取
 * @Target(ElementType.METHOD) 表示该注解可应用于方法上
 * @Documented 表示该注解会包含到Javadoc中
 * @Component 表示该注解是一个组件，可以被Spring容器识别
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Component
public @interface ApiLimitedRole {

    /**
     * 限制访问的角色代码列表。
     * 方法被此注解标记后，只有拥有列表中指定角色代码的用户才能访问。
     * 默认为空数组，即没有访问限制。
     *
     * @return String[] 限制访问的角色代码列表
     */
    String[] limitedRoleCodeList() default {};
}
