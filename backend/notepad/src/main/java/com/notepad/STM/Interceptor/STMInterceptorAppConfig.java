package com.notepad.STM.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class STMInterceptorAppConfig implements WebMvcConfigurer {
	 @Autowired
	 STMAppInterceptor appinterceptor;

	 @Override
	 public void addInterceptors(InterceptorRegistry registry) {
	      registry.addInterceptor(appinterceptor);
	   }
}