package com.uptask.api.Configs;

import com.uptask.api.Interceptors.ProjectExistInterceptor;
import com.uptask.api.Interceptors.TaskExistInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.origin}")
    private String corsOrigin;

    @Autowired
    private ProjectExistInterceptor projectExistInterceptor;

    @Autowired
    private TaskExistInterceptor taskExistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(projectExistInterceptor).addPathPatterns("/api/projects/*/**");
        registry.addInterceptor(taskExistInterceptor).addPathPatterns("/api/projects/*/tasks/*/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
