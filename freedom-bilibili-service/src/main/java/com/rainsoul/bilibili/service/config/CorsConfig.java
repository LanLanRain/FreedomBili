package com.rainsoul.bilibili.service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 跨域解决配置
 * <p>
 * 跨域概念：
 * 出于浏览器的同源策略限制，同源策略会阻止一个域的javascript脚本和另外一个域的内容进行交互。
 * 所谓同源就是指两个页面具有相同的协议（protocol），主机（host）和端口号（port）
 * <p>
 * 非同源的限制：
 * 【1】无法读取非同源网页的 Cookie、LocalStorage 和 IndexedDB
 * 【2】无法接触非同源网页的 DOM
 * 【3】无法向非同源地址发送 AJAX 请求
 * <p>
 * spingboot解决跨域方案：CORS 是跨域资源分享（Cross-Origin Resource Sharing）的缩写。
 * 它是 W3C 标准，属于跨源 AJAX 请求的根本解决方法。
 * <p>
 * <p>
 * Filter是用来过滤任务的，既可以被使用在请求资源，也可以是资源响应，或者二者都有
 * Filter使用doFilter方法进行过滤
 */

@Configuration // 表示这是一个配置类
public class CorsConfig implements Filter {

    // 定义允许跨域请求的域名数组
    private final String[] allowedDomain = {"http://localhost:8080", "http://39.107.54.180"};

    /**
     * 处理跨域请求。
     *
     * @param request  ServletRequest，客户端请求
     * @param response ServletResponse，服务器响应
     * @param chain    FilterChain，过滤器链，用于将请求传递给下一个过滤器或servlet
     * @throws IOException 如果发生I/O错误
     * @throws ServletException 如果发生servlet相关错误
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 创建允许的跨域请求源集合
        Set<String> allowedOrigins = new HashSet<>(Arrays.asList(allowedDomain));

        // 获取并处理请求头部中的Origin字段
        String origin = httpRequest.getHeader("Origin");
        if (origin == null) {
            chain.doFilter(request, response); // 如果没有Origin头，直接传递请求
            return;
        }

        // 检查是否为允许的跨域请求源
        if (allowedOrigins.contains(origin)) {
            // 设置跨域响应头
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, userId, token, ut");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("XDomainRequestAllowed", "1");
        }

        chain.doFilter(request, response); // 继续传递请求
    }
}
