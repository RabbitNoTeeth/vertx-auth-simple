package fun.bookish.vertx.auth.simple.config;

import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.encryption.SimpleAuthEncryption;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;

/**
 * 配置类
 */
public class SimpleAuthOptions {

    private String jsessionIdCookieKey = SimpleAuthConstants.JSESSIONID_COOKIE_KEY;
    private String rememberMeCookieKey = SimpleAuthConstants.REMEMBERME_COOKIE_KEY;
    private String rememberMeKey = SimpleAuthConstants.REMEMBERME_KEY;
    private long sessionTimeout = SimpleAuthConstants.DEFAULT_SESSION_TIMEOUT;
    private long rememberMeTimeout = SimpleAuthConstants.DEFAULT_REMEMBERME_TIMEOUT;
    private String encryptionKey = SimpleAuthConstants.DEFAULT_ENCRYPT_KEY;
    private PermissionStrategy permissionStrategy = SimpleAuthConstants.DEFAULT_PERMISSION_STRATEGY;
    private SimpleAuthEncryption simpleEncryption = SimpleAuthConstants.DEFAULT_ENCRYPTION_STRATEGY;


    public SimpleAuthOptions copy(){
        return new SimpleAuthOptions().setJsessionIdCookieKey(this.jsessionIdCookieKey)
                .setRememberMeCookieKey(this.rememberMeCookieKey)
                .setRememberMeKey(this.rememberMeKey)
                .setSessionTimeout(this.sessionTimeout)
                .setRememberMeTimeout(this.rememberMeTimeout)
                .setEncryptionKey(this.encryptionKey)
                .setPermissionStrategy(this.permissionStrategy)
                .setSimpleEncryption(this.simpleEncryption);
    }

    public String getJsessionIdCookieKey() {
        return jsessionIdCookieKey;
    }

    public SimpleAuthOptions setJsessionIdCookieKey(String jsessionIdCookieKey) {
        this.jsessionIdCookieKey = jsessionIdCookieKey;
        return this;
    }

    public String getRememberMeCookieKey() {
        return rememberMeCookieKey;
    }

    public SimpleAuthOptions setRememberMeCookieKey(String rememberMeCookieKey) {
        this.rememberMeCookieKey = rememberMeCookieKey;
        return this;
    }

    public String getRememberMeKey() {
        return rememberMeKey;
    }

    public SimpleAuthOptions setRememberMeKey(String rememberMeKey) {
        this.rememberMeKey = rememberMeKey;
        return this;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public SimpleAuthOptions setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
        return this;
    }

    public long getRememberMeTimeout() {
        return rememberMeTimeout;
    }

    public SimpleAuthOptions setRememberMeTimeout(long rememberMeTimeout) {
        this.rememberMeTimeout = rememberMeTimeout;
        return this;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public SimpleAuthOptions setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return this;
    }

    public PermissionStrategy getPermissionStrategy() {
        return permissionStrategy;
    }

    public SimpleAuthOptions setPermissionStrategy(PermissionStrategy permissionStrategy) {
        this.permissionStrategy = permissionStrategy;
        return this;
    }

    public SimpleAuthEncryption getSimpleEncryption() {
        return simpleEncryption;
    }

    public SimpleAuthOptions setSimpleEncryption(SimpleAuthEncryption simpleEncryption) {
        this.simpleEncryption = simpleEncryption;
        return this;
    }
}
