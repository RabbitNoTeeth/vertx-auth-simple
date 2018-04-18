package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.configurable.RealmStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;

import java.util.List;

/**
 * 配置类
 */
public class SimpleAuthOptions {

    private PermissionStrategy permissionStrategy;
    private SessionIdStrategy sessionIdStrategy;
    private SessionPersistStrategy sessionPersistStrategy;
    private RealmStrategy realmStrategy;
    private List<String> annoPermissions;

    public PermissionStrategy getPermissionStrategy() {
        return permissionStrategy;
    }

    public void setPermissionStrategy(PermissionStrategy permissionStrategy) {
        this.permissionStrategy = permissionStrategy;
    }

    public SessionIdStrategy getSessionIdStrategy() {
        return sessionIdStrategy;
    }

    public void setSessionIdStrategy(SessionIdStrategy sessionIdStrategy) {
        this.sessionIdStrategy = sessionIdStrategy;
    }

    public SessionPersistStrategy getSessionPersistStrategy() {
        return sessionPersistStrategy;
    }

    public void setSessionPersistStrategy(SessionPersistStrategy sessionPersistStrategy) {
        this.sessionPersistStrategy = sessionPersistStrategy;
    }

    public RealmStrategy getRealmStrategy() {
        return realmStrategy;
    }

    public void setRealmStrategy(RealmStrategy realmStrategy) {
        this.realmStrategy = realmStrategy;
    }

    public List<String> getAnnoPermissions() {
        return annoPermissions;
    }

    public void setAnnoPermissions(List<String> annoPermissions) {
        this.annoPermissions = annoPermissions;
    }
}
