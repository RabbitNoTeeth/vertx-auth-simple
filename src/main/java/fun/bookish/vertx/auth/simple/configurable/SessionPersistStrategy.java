package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.ext.web.Session;

/**
 * sessionc持久化策略接口
 */
public interface SessionPersistStrategy {

    /**
     * 缓存session
     * @param session
     */
    void cache(Session session);

    /**
     * 获取session
     * @param sessionId
     * @return
     */
    Session get(String sessionId);

    /**
     * 删除session
     * @param session
     */
    void remove(Session session);

    /**
     * 清空所有缓存的session
     */
    void clearAll();

}
