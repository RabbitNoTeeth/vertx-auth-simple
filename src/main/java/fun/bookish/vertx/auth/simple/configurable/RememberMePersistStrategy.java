package fun.bookish.vertx.auth.simple.configurable;

import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Session;

/**
 * rememberMe cookie持久化策略接口
 */
public interface RememberMePersistStrategy {

    /**
     * 缓存session
     * @param rememberMeCookie
     * @param session
     */
    void cache(Cookie rememberMeCookie, Session session);

    /**
     * 获取session
     * @param rememberMeCookie
     * @return
     */
    Session get(Cookie rememberMeCookie);

    /**
     * 删除session
     * @param rememberMeCookie
     */
    void remove(Cookie rememberMeCookie);

    /**
     * 清除过期的rememberme session
     */
    void clearExpired();

    /**
     * 清空所有缓存
     */
    void clearAll();

    RememberMePersistStrategy setOptions(SimpleAuthOptions options);

}
