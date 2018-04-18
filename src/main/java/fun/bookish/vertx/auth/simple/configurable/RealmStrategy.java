package fun.bookish.vertx.auth.simple.configurable;


import io.vertx.ext.web.RoutingContext;

/**
 * 权限校验结果处理策略接口
 */
public interface RealmStrategy {

    /**
     * 当请求通过权限验证后，在调用RoutingContext.next()进入下一个处理器之前调用
     * @param context
     */
    void afterAuthorisedSucceed(RoutingContext context);

    /**
     * 当用户已登录，但是权限验证失败（不具有当前请求资源的权限）时调用
     * @param context
     */
    void handleAuthorisedFailed(RoutingContext context);

    /**
     * 当用户未登录时调用
     * @param context
     */
    void handleAuthenticatedFailed(RoutingContext context);

}
