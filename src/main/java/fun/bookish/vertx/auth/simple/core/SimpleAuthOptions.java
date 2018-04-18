package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.configurable.RealmStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;

import java.util.List;

/**
 * 配置类
 */
public class SimpleAuthOptions {

    private PermissionStrategy permissionStrategy = SimpleAuthConstants.DEFAULT_PERMISSION_STRATEGY_IMPL;
    private SessionIdStrategy sessionIdStrategy = SimpleAuthConstants.DEFAULT_SESSION_ID_STRATEGY_IMPL;
    private SessionPersistStrategy sessionPersistStrategy = SimpleAuthConstants.DEFAULT_SESSION_PERSIST_STRATEGY_IMPL;
    private RealmStrategy realmStrategy = SimpleAuthConstants.DEFAULT_REALM_STRATEGY_IMPL;
    private List<String> annoPermissions;

    public PermissionStrategy getPermissionStrategy() {
        return permissionStrategy;
    }

    public void setPermissionStrategy(PermissionStrategy permissionStrategy) {
        if(permissionStrategy == null){
            throw new IllegalArgumentException("permissionStrategy can not be null");
        }
        this.permissionStrategy = permissionStrategy;
    }

    public SessionIdStrategy getSessionIdStrategy() {
        return sessionIdStrategy;
    }

    public void setSessionIdStrategy(SessionIdStrategy sessionIdStrategy) {
        if(sessionIdStrategy == null){
            throw new IllegalArgumentException("sessionIdStrategy can not be null");
        }
        this.sessionIdStrategy = sessionIdStrategy;
    }

    public SessionPersistStrategy getSessionPersistStrategy() {
        return sessionPersistStrategy;
    }

    public void setSessionPersistStrategy(SessionPersistStrategy sessionPersistStrategy) {
        if(sessionPersistStrategy == null){
            throw new IllegalArgumentException("sessionPersistStrategy can not be null");
        }
        this.sessionPersistStrategy = sessionPersistStrategy;
    }

    public RealmStrategy getRealmStrategy() {
        return realmStrategy;
    }

    public void setRealmStrategy(RealmStrategy realmStrategy) {
        if(realmStrategy == null){
            throw new IllegalArgumentException("realmStrategy can not be null");
        }
        this.realmStrategy = realmStrategy;
    }

    public List<String> getAnnoPermissions() {
        return annoPermissions;
    }

    public void setAnnoPermissions(List<String> annoPermissions) {
        this.annoPermissions = annoPermissions;
    }

}
