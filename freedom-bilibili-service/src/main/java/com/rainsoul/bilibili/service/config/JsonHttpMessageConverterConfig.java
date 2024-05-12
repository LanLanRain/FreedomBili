package com.rainsoul.bilibili.service.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class JsonHttpMessageConverterConfig {




    /**
     * 配置并返回FastJsonHttpMessageConverter，用于处理HTTP消息的转换。
     * 该方法配置了一个FastJson转换器，并设置了FastJson的序列化配置。
     * 由于标注了@Primary，所以该转换器在所有其他转换器之前被使用。
     *
     * @return HttpMessageConverters 包含一个配置好的FastJsonHttpMessageConverter实例。
     */
    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        // 配置FastJson序列化设置
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式
        fastJsonConfig.setSerializerFeatures(
                // 配置序列化效果
                SerializerFeature.PrettyFormat, // 美化格式
                SerializerFeature.WriteNullStringAsEmpty, // 空字符串设置为空
                SerializerFeature.WriteNullListAsEmpty, // 空列表设置为空
                SerializerFeature.WriteMapNullValue, // 映射空值设置为null
                SerializerFeature.MapSortField, // 键排序
                SerializerFeature.DisableCircularReferenceDetect // 禁止循环引用 todo 1.学习循环引用
        );

        fastConverter.setFastJsonConfig(fastJsonConfig); // 应用FastJson配置
        return new HttpMessageConverters(fastConverter); // 返回包含FastJson转换器的HttpMessageConverters
    }

}
