package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import io.vertx.ext.web.Session;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的session持久化策略接口实现
 */
public class DefaultSessionPersistStrategyImpl implements SessionPersistStrategy {

    private final ConcurrentHashMap<String,Session> CACHE_MAP = new ConcurrentHashMap<>();


    @Override
    public void cache(Session session) {
        CACHE_MAP.put(session.id(),session);
    }

    @Override
    public Session get(String sessionId) {
        return CACHE_MAP.get(sessionId);
    }

    @Override
    public void remove(Session session) {
        CACHE_MAP.remove(session.id());
    }

    @Override
    public void clearAll() {
        CACHE_MAP.clear();
    }
}
