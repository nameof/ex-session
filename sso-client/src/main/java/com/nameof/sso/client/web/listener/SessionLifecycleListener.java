package com.nameof.sso.client.web.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.nameof.sso.client.web.session.LogedSessionManager;

/**
 * 监听session的过期或销毁，从{@link LogedSessionManager}中移除session
 * 
 * @author ChengPan
 */
public class SessionLifecycleListener implements HttpSessionListener {

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String gloabSessionId = (String) session.getAttribute("gloabSessionId");
		if (gloabSessionId != null) {
			LogedSessionManager.detach(gloabSessionId.toString());
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {}
}
