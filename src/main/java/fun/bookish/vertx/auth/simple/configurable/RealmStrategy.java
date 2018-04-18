package fun.bookish.vertx.auth.simple.configurable;


import io.vertx.ext.web.RoutingContext;

public interface RealmStrategy {

    void afterAuthorisedSucceed(RoutingContext context);

    void handleAuthorisedFailed(RoutingContext context);

    void handleAuthenticatedFailed(RoutingContext context);

}
