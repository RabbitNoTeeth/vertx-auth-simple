package fun.bookish.vertx.auth.simple.constant;

/**
 * 配置键值常量
 */
public enum SimpleAuthConfigKey {

    SESSION_TIMEOUT("sessionTimeout"),
    REMEMBERME_COOKIE_TIMEOUT("rememberMeTimeout"),
    ENCRYPT_KEY("EncryptKey");

    private final String value;
    SimpleAuthConfigKey(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }

}
