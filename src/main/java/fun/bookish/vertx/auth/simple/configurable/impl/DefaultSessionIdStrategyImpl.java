package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * 默认的sessionId策略接口实现
 */
public class DefaultSessionIdStrategyImpl implements SessionIdStrategy{


    @Override
    public String getSessionId(RoutingContext context) {
        return context.getCookie("JSESSIONID") == null? null : context.getCookie("JSESSIONID").getValue();
    }

    @Override
    public void writeSessionId(String sessionId, RoutingContext context) {
        Cookie cookie = Cookie.cookie("JSESSIONID",sessionId).setHttpOnly(true).setPath("/");
        context.addCookie(cookie);
    }
}
