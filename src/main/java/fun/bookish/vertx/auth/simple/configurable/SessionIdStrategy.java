package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.ext.web.RoutingContext;

/**
 * sessionId获取和写入策略
 */
public interface SessionIdStrategy {

    /**
     * 从RoutingContext中获取sessionId
     * @param context
     * @return
     */
    String getSessionId(RoutingContext context);

    /**
     * 将sessionId写入到RoutingContext中。
     *      方法详解：用户首次访问时，会创建一个session，并且生成对应的sessionId，simple-auth在每次进行
     *               权限校验时，都会从请求中获取sessionId，进而获取到session，然后验证权限。该sessionId
     *               可以是通过cookie来交互，或者请求的header，或者url参数等
     * @param sessionId
     * @param context
     */
    void writeSessionId(String sessionId,RoutingContext context);

}
