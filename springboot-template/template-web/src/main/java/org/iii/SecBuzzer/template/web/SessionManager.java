package org.iii.SecBuzzer.template.web;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;

import org.springframework.security.web.session.HttpSessionEventPublisher;
/**
 * 線上人數管理
 */
@WebListener
public class SessionManager extends HttpSessionEventPublisher {

	private static int onlineUserCount;
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		onlineUserCount++;
		super.sessionCreated(event);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		onlineUserCount--;
		super.sessionDestroyed(event);
	}

	public static int getUserCount() {
		if (onlineUserCount <= 0)
			onlineUserCount = 0;
		return onlineUserCount + 1;
	}
}