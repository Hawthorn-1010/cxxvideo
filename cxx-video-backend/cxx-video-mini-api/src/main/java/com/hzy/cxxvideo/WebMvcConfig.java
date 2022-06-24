package com.hzy.cxxvideo;

import com.hzy.cxxvideo.controller.interceptor.MiniInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/12 16:58
 **/

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 资源映射，/**访问所有资源
        registry.addResourceHandler("/**")
                // 继承WebMvcConfigurationSupport的过滤器导致Swagger地址不能访问,所以要加这一句
                .addResourceLocations("classpath:/META-INF/resources/")
                // 要记得加 file:
                .addResourceLocations("file:D:/cxx-video/");
    }

    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }



//    // 注册到Spring
//    @Bean
//    public MiniInterceptor miniInterceptor() {
//        return new MiniInterceptor();
//    }

//    // 注册到拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 注册
//        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**");
//        super.addInterceptors(registry);
//    }
}










