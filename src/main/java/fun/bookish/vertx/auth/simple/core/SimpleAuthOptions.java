package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.configurable.*;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultSessionPersistStrategyImpl;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import io.vertx.core.Vertx;

import java.util.List;

/**
 * 配置类
 */
public class SimpleAuthOptions {

    private Vertx vertx;
    private PermissionStrategy permissionStrategy = SimpleAuthConstants.DEFAULT_PERMISSION_STRATEGY_IMPL;
    private SessionIdStrategy sessionIdStrategy = SimpleAuthConstants.DEFAULT_SESSION_ID_STRATEGY_IMPL;
    private SessionPersistStrategy sessionPersistStrategy = new DefaultSessionPersistStrategyImpl().setOptions(this);
    private RememberMePersistStrategy rememberMePersistStrategy;
    private RealmStrategy realmStrategy = SimpleAuthConstants.DEFAULT_REALM_STRATEGY_IMPL;
    private Long sessionTimeout = SimpleAuthConstants.DEFAULT_SESSION_TIMEOUT;
    private Long rememberMeTimeout = SimpleAuthConstants.DEFAULT_REMEMBER_ME_TIMEOUT;
    private List<String> annoPermissions;

    public PermissionStrategy getPermissionStrategy() {
        return permissionStrategy;
    }

    public SimpleAuthOptions setPermissionStrategy(PermissionStrategy permissionStrategy) {
        if(permissionStrategy == null){
            throw new IllegalArgumentException("permissionStrategy can not be null");
        }
        this.permissionStrategy = permissionStrategy;
        return this;
    }

    public SessionIdStrategy getSessionIdStrategy() {
        return sessionIdStrategy;
    }

    public SimpleAuthOptions setSessionIdStrategy(SessionIdStrategy sessionIdStrategy) {
        if(sessionIdStrategy == null){
            throw new IllegalArgumentException("sessionIdStrategy can not be null");
        }
        this.sessionIdStrategy = sessionIdStrategy;
        return this;
    }

    public SessionPersistStrategy getSessionPersistStrategy() {
        return sessionPersistStrategy;
    }

    public SimpleAuthOptions setSessionPersistStrategy(SessionPersistStrategy sessionPersistStrategy) {
        if(sessionPersistStrategy == null){
            throw new IllegalArgumentException("sessionPersistStrategy can not be null");
        }
        this.sessionPersistStrategy = sessionPersistStrategy;
        return this;
    }

    public RealmStrategy getRealmStrategy() {
        return realmStrategy;
    }

    public SimpleAuthOptions setRealmStrategy(RealmStrategy realmStrategy) {
        if(realmStrategy == null){
            throw new IllegalArgumentException("realmStrategy can not be null");
        }
        this.realmStrategy = realmStrategy;
        return this;
    }

    public List<String> getAnnoPermissions() {
        return annoPermissions;
    }

    public SimpleAuthOptions setAnnoPermissions(List<String> annoPermissions) {
        this.annoPermissions = annoPermissions;
        return this;
    }

    public Long getSessionTimeout() {
        return sessionTimeout;
    }

    public SimpleAuthOptions setSessionTimeout(Long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public SimpleAuthOptions setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    public Long getRememberMeTimeout() {
        return rememberMeTimeout;
    }

    public SimpleAuthOptions setRememberMeTimeout(Long rememberMeTimeout) {
        this.rememberMeTimeout = rememberMeTimeout;
        return this;
    }

    public RememberMePersistStrategy getRememberMePersistStrategy() {
        return rememberMePersistStrategy;
    }

    public SimpleAuthOptions setRememberMePersistStrategy(RememberMePersistStrategy rememberMePersistStrategy) {
        if(rememberMePersistStrategy == null){
            throw new IllegalArgumentException("rememberMePersistStrategy can not be null");
        }
        this.rememberMePersistStrategy = rememberMePersistStrategy;
        return this;
    }
}
