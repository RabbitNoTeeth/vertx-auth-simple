package fun.bookish.vertx.auth.simple.constant;

import fun.bookish.vertx.auth.simple.encryption.DefaultAESAuthEncryption;
import fun.bookish.vertx.auth.simple.encryption.SimpleAuthEncryption;
import fun.bookish.vertx.auth.simple.ext.DefaultPermissionStrategyImpl;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;

/**
 * 常量
 */
public class SimpleAuthConstants {

    private SimpleAuthConstants(){}

    public static final String JSESSIONID_COOKIE_KEY = "simple-auth.JSESSIONID";
    public static final String REMEMBERME_COOKIE_KEY = "simple-auth.RememberMe";
    public static final String VERTX_CTX_SECURITY_MANAGER_KEY = "simple-auth.SecurityManager";
    public static final String ROUTER_CTX_SUBJECT_KEY = "simple-auth.SUBJECT";
    public static final String ROUTER_CTX_START_TIME_KEY = "simple-auth.START_TIME";
    public static final String REMEMBERME_KEY = "RememberMe";
    public static final PermissionStrategy DEFAULT_PERMISSION_STRATEGY = new DefaultPermissionStrategyImpl();
    public static final  SimpleAuthEncryption DEFAULT_ENCRYPTION_STRATEGY = new DefaultAESAuthEncryption();

    //默认sessionId cookie过期时间
    public static final long DEFAULT_SESSION_TIMEOUT = 3600L;
    //默认rememberMe cookie过期时间
    public static final long DEFAULT_REMEMBERME_TIMEOUT = 1296000L;
    //默认rememberMe cookie加密密钥
    public static final String DEFAULT_ENCRYPT_KEY = "iz616ek8il873yf6==QQq";

}
