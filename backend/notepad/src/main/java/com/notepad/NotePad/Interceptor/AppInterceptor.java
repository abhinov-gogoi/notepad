package com.notepad.NotePad.Interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		/**
		 * Hold the Api Url
		 */
		StringBuffer api_url = request.getRequestURL();

		/**
		 * Check for the Null
		 */
		if (request.getHeader("User-Agent") == null || request.getHeader("User-Agent").trim().equals("")) {
			return false;
		}
		return true;
	}
}
