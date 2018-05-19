package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import io.vertx.ext.web.Session;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 默认的session持久化策略接口实现
 */
public class DefaultSessionPersistStrategyImpl implements SessionPersistStrategy {

    private SimpleAuthOptions options;

    private static final ConcurrentHashMap<String,Session> SESSION_CACHE = new ConcurrentHashMap<>();

    private static final AtomicBoolean CLEAR_EXPIRED_TASK_STARTED = new AtomicBoolean(false);

    @Override
    public void cache(Session session) {
        checkClearExpiredTask();
        SESSION_CACHE.put(session.id(),session);
    }

    @Override
    public Session get(String sessionId) {
        checkClearExpiredTask();
        return sessionId == null ? null : SESSION_CACHE.get(sessionId);
    }

    @Override
    public void remove(Session session) {
        checkClearExpiredTask();
        SESSION_CACHE.remove(session.id());
    }

    @Override
    public void clearExpired() {
        Long sessionTimeout = this.options.getSessionTimeout();
        clearExpiredSession(sessionTimeout);
    }

    @Override
    public void clearAll() {
        checkClearExpiredTask();
        SESSION_CACHE.clear();
    }

    @Override
    public SessionPersistStrategy setOptions(SimpleAuthOptions options) {
        this.options = options;
        return this;
    }

    private void checkClearExpiredTask(){
        if(!CLEAR_EXPIRED_TASK_STARTED.get()){
            Long sessionTimeout = this.options.getSessionTimeout();
            this.options.getVertx().setPeriodic(sessionTimeout*1000, id -> {
                clearExpiredSession(sessionTimeout);
            });
        }
    }

    private void clearExpiredSession(Long sessionTimeOut){
        LocalDateTime now = LocalDateTime.now();
        Set<Map.Entry<String, Session>> entrySet = SESSION_CACHE.entrySet();
        for(Map.Entry<String, Session> entry:entrySet){
            LocalDateTime sessionCreate = entry.getValue().get(SimpleAuthConstants.SESSION_CREATE_TIME_KEY);
            if(sessionCreate.plusSeconds(sessionTimeOut).isBefore(now)){
                SESSION_CACHE.remove(entry.getKey());
            }
        }
    }
}
