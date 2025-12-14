package com.example.shop.config;

import com.example.shop.interceptor.AdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 只要访问 /admin/... 开头的路径，都必须通过 AdminInterceptor 检查
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**");
    }
}