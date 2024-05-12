package com.rainsoul.bilibili.dao.domain;

/**
 * 用于封装API响应的类。
 * @param <T> 响应数据的类型
 */
public class JsonResponse<T> {

    private String code; // 响应代码
    private String message; // 响应消息
    private T data; // 响应数据

    /**
     * 构造一个包含响应代码和消息的JsonResponse。
     * @param code 响应代码。
     * @param message 响应消息。
     */
    public JsonResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造一个成功响应，包含数据。
     * @param data 响应数据。
     */
    public JsonResponse(T data) {
        this.data = data;
        this.code = "0"; // 默认成功代码
        this.message = "success"; // 默认成功消息
    }

    /**
     * 构造一个空的成功响应。
     * @return JsonResponse<String> 成功响应实例，无数据。
     */
    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    /**
     * 构造一个包含指定数据的成功响应。
     * @param data 响应数据。
     * @return JsonResponse<String> 成功响应实例，包含数据。
     */
    public static JsonResponse<String> success(String data) {
        return new JsonResponse<>(data);
    }

    /**
     * 构造一个失败响应。
     * @return JsonResponse<String> 失败响应实例，无数据。
     */
    public static JsonResponse<String> fail() {
        return new JsonResponse<>("-1", "fail");
    }

    /**
     * 构造一个包含指定响应代码和消息的失败响应。
     * @param code 响应代码。
     * @param msg 响应消息。
     * @return JsonResponse<String> 失败响应实例，无数据。
     */
    public static JsonResponse<String> fail(String code, String msg) {
        return new JsonResponse<>(code, msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
