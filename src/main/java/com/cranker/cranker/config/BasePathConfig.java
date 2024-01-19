package com.cranker.cranker.config;

import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class BasePathConfig {

    @ControllerAdvice(annotations = RestController.class)
    public static class BasePathAdvice extends RequestMappingHandlerMapping {

        @Override
        public RequestMappingInfo getMappingForMethod(@NonNull Method method, @NonNull Class<?> handlerType) {
            RequestMappingInfo mappingForMethod = super.getMappingForMethod(method, handlerType);
            if (mappingForMethod != null) {
                String basePath = "api";
                return RequestMappingInfo.paths(basePath).build().combine(mappingForMethod);
            }
            return null;
        }
    }
}
