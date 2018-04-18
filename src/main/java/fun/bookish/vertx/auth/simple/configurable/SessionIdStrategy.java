package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.ext.web.RoutingContext;

/**
 * SessionId策略接口
 */
public interface SessionIdStrategy {

    String getSessionId(RoutingContext context);

    void writeSessionId(RoutingContext context);

}
