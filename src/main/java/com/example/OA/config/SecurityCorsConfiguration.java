package com.example.OA.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by aa on 2017/11/20.
 */

/*
解决跨域问题
@Configuration可理解为用spring的时候xml里面的<beans>标签
@Bean可理解为用spring的时候xml里面的<bean>标签
 */
@Configuration
public class SecurityCorsConfiguration {

    /*
    FilterRegistrationBean ：setOrder方法，可以为filter设置排序值，
    让spring在注册filter之前排序后再依次注册。
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        System.out.println("-------------->CorsFilter started !!!");
        return bean;
    }
}
