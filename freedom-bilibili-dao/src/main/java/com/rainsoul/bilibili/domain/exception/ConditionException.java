/**
 * 自定义条件查询异常类，用于处理在条件查询过程中出现的异常。
 * 继承自 RuntimeException，表示这是一种运行时异常。
 */
package com.rainsoul.bilibili.domain.exception;

public class ConditionException extends RuntimeException {
    public static final long serialVersionUID = 1L; // 序列化ID，用于版本控制。

    private String code; // 异常代码，用于标识异常的类型。

    /**
     * 带有错误代码和错误信息的构造函数。
     *
     * @param code 异常代码，用于标识异常的类型。
     * @param name 异常名称或信息，描述异常的具体问题。
     */
    public ConditionException(String code, String name) {
        super(name); // 调用父类构造函数，设置异常名称。
        this.code = code; // 初始化异常代码。
    }

    /**
     * 带有错误信息的构造函数，错误代码默认为 "500"。
     *
     * @param name 异常名称或信息，描述异常的具体问题。
     */
    public ConditionException(String name) {
        super(name); // 调用父类构造函数，设置异常名称。
        code = "500"; // 默认异常代码。
    }

    /**
     * 获取异常代码。
     *
     * @return 异常代码字符串。
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置异常代码。
     *
     * @param code 要设置的异常代码。
     */
    public void setCode(String code) {
        this.code = code; // 更新异常代码。
    }
}
