package com.server.aquacultureserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限注解
 * 用于标记需要特定权限才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * 允许访问的权限代码数组
     */
    String[] value();
    
    /**
     * 错误提示信息
     */
    String message() default "您没有权限执行此操作";
}

