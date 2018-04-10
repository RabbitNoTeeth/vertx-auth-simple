package fun.bookish.vertx.auth.simple.config;

/**
 * 配置键值常量
 */
public enum SimpleAuthConfigKey {

    // session过期时长
    SESSION_TIMEOUT("sessionTimeout"),

    // cookie过期时长
    REMEMBERME_COOKIE_TIMEOUT("rememberMeTimeout"),

    // rememberMe cookie加密方式
    ENCRYPT_TYPE("encryptType"),

    // rememberMe cookie加密密钥
    ENCRYPT_KEY("encryptKey"),

    // 访问路由校验接口
    PERMISSION_STRATEGY("permissionStrategy");

    private final String value;
    SimpleAuthConfigKey(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }

}
