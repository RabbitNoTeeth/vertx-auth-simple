package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.ext.web.Session;

/**
 * session持久化策略
 */
public interface SessionPersistStrategy {

    void cache(Session session);

    Session get(String sessionId);

    void remove(Session session);

    void clearAll();

}
