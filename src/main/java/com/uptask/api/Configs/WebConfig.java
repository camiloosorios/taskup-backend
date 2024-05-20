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

    @Autowired
    private ProjectExistInterceptor ProjectExistInterceptor;

    @Autowired
    private TaskExistInterceptor TaskExistInterceptor;

    @Value("${cors.origin}")
    private String corsOrigin;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ProjectExistInterceptor)
                .addPathPatterns("/api/projects/*/**");
        registry.addInterceptor(TaskExistInterceptor)
                .addPathPatterns("/api/projects/*/tasks/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
