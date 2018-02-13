package fun.bookish.vertx.auth.simple.constant;

/**
 * 常量
 */
public class SimpleConstants {

    private SimpleConstants(){}

    public static final String COOKIE_JSESSIONID_KEY = "simple-auth.JSESSIONID";
    public static final String COOKIE_REMEMBERME_KEY = "simple-auth.RememberMe";

    public static final String VERTX_CTX_SECURITY_MANAGER_KEY = "simple-auth.SecurityManager";

    public static final String ROUTER_CTX_SUBJECT_KEY = "simple-auth.SUBJECT";
    public static final String ROUTER_CTX_START_TIME_KEY = "simple-auth.START_TIME";

    public static final String PRINCIPAL_PERMISSION_KEY = "cachedPermissions";

    public static final String AUTH_REMEMBERME_KEY = "RememberMe";


    //默认sessionId cookie过期时间
    public static final Long DEFAULT_SESSION_TIMEOUT = 3600L;
    //默认rememberMe cookie过期时间
    public static final Long DEFAULT_REMEMBERME_TIMEOUT = 1296000L;
    //默认rememberMe cookie加密密钥
    public static final String DEFAULT_AES_KEY = "iz616ek8il873yf6==QQq";

}
