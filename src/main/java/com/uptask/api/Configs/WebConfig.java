package com.uptask.api.Configs;

import com.uptask.api.Interceptors.ProjectExistInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectExistInterceptor ProjectExistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ProjectExistInterceptor)
                .addPathPatterns("/api/projects/*/**");
    }
}
