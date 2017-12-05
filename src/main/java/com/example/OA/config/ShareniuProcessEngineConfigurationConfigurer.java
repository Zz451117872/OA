package com.example.OA.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.stereotype.Component;

/**
 * Created by aa on 2017/11/29.
 */

/*
解决activiti流程图中文乱码问题
 */
@Component
public class ShareniuProcessEngineConfigurationConfigurer implements ProcessEngineConfigurationConfigurer {
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        System.out.println("ShareniuProcessEngineConfigurationConfigurer#############");
        System.out.println(processEngineConfiguration.getActivityFontName());
    }
}
