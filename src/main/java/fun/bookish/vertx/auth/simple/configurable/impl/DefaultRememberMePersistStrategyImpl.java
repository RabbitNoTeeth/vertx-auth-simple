package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.RememberMePersistStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Session;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rememberMe cookie默认持久化策略
 */
public class DefaultRememberMePersistStrategyImpl implements RememberMePersistStrategy {

    private SimpleAuthOptions options;

    private static final ConcurrentHashMap<String,Session> SESSION_CACHE = new ConcurrentHashMap<>();

    private AtomicBoolean clearExpiredTaskStarted = new AtomicBoolean(false);

    @Override
    public void cache(Cookie rememberMeCookie, Session session) {
        checkClearExpiredTask();
        SESSION_CACHE.put(rememberMeCookie.getValue(),session);
    }

    @Override
    public Session get(Cookie rememberMeCookie) {
        checkClearExpiredTask();
        return SESSION_CACHE.get(rememberMeCookie.getValue());
    }

    @Override
    public void remove(Cookie rememberMeCookie) {
        checkClearExpiredTask();
        SESSION_CACHE.remove(rememberMeCookie.getValue());
    }

    @Override
    public void clearExpired() {
        Long cookieTimeout = this.options.getSessionTimeout();
        clearExpiredSession(cookieTimeout);
    }

    @Override
    public void clearAll() {
        checkClearExpiredTask();
        SESSION_CACHE.clear();
    }

    @Override
    public RememberMePersistStrategy setOptions(SimpleAuthOptions options) {
        this.options = options;
        return this;
    }

    private void checkClearExpiredTask(){
        if(!clearExpiredTaskStarted.get()){
            Long cookieTimeout = this.options.getRememberMeTimeout();
            this.options.getVertx().setPeriodic(cookieTimeout*1000, id -> {
                clearExpiredSession(cookieTimeout);
            });
        }
    }

    private void clearExpiredSession(Long cookieTimeout){
        LocalDateTime now = LocalDateTime.now();
        Set<Map.Entry<String, Session>> entrySet = SESSION_CACHE.entrySet();
        for(Map.Entry<String, Session> entry:entrySet){
            LocalDateTime sessionCreate = entry.getValue().get(SimpleAuthConstants.SESSION_CREATE_TIME_KEY);
            if(sessionCreate.plusSeconds(cookieTimeout).isBefore(now)){
                SESSION_CACHE.remove(entry.getKey());
            }
        }
    }
}
