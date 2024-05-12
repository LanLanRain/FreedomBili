package com.rainsoul.bilibili.service.handler;

import com.rainsoul.bilibili.dao.domain.JsonResponse;
import com.rainsoul.bilibili.dao.domain.exception.ConditionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类，用于捕获控制器层抛出的异常，并返回统一格式的响应体。
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {

    /**
     * 处理所有未被其他异常处理器捕获的异常。
     *
     * @param request 当前HTTP请求对象，用于获取请求信息。
     * @param e 抛出的异常对象。
     * @return 返回一个封装了异常信息的JsonResponse对象。
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String eMessage = e.getMessage(); // 获取异常信息
        if(e instanceof ConditionException){ // 判断是否为自定义的ConditionException异常
            String code = ((ConditionException) e).getCode(); // 获取自定义异常的错误码
            return new JsonResponse<>(code,eMessage); // 返回自定义异常信息
        }else{
            // 对于非自定义异常，统一返回500错误码
            return new JsonResponse<>("500",eMessage);
        }
    }
}
