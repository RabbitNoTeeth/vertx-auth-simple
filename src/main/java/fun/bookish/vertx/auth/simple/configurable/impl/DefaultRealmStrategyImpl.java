package fun.bookish.vertx.auth.simple.configurable.impl;


import fun.bookish.vertx.auth.simple.configurable.RealmStrategy;
import io.vertx.ext.web.RoutingContext;

/**
 * 权限校验结果处理策略接口默认实现
 */
public class DefaultRealmStrategyImpl implements RealmStrategy {

    @Override
    public void afterAuthorisedSucceed(RoutingContext context) {

    }

    @Override
    public void handleAuthorisedFailed(RoutingContext context) {
        context.response().setStatusCode(403).end("you have no permission to access '"+context.request().path()+"'");
    }

    @Override
    public void handleAuthenticatedFailed(RoutingContext context) {
        context.response().setStatusCode(403).end("you need login first");
    }

}
