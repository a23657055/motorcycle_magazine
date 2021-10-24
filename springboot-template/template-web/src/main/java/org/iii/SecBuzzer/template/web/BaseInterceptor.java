package org.iii.SecBuzzer.template.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 方法注入攔截器(需要注入的Controller要修改servelet-context.xml)
 */
@Component
public class BaseInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 在方法執行前攔截
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            response
	 * @param handler
	 *            handler
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		String controllerName = "";
		String actionName = "";
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			controllerName = handlerMethod.getBeanType().getSimpleName().replace("Direct", "").replace("Controller", "");
			actionName = handlerMethod.getMethod().getName();
		}
		session.setAttribute("controllerName", controllerName);
		session.setAttribute("actionName", actionName);
		return super.preHandle(request, response, handler);
	}
}