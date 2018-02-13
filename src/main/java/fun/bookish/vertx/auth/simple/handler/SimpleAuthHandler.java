package fun.bookish.vertx.auth.simple.handler;


import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public interface SimpleAuthHandler extends Handler<RoutingContext> {

    static SimpleAuthHandler create(Vertx vertx, SimpleAuthProvider simpleAuthProvider){
        return new SimpleAuthHandlerImpl(vertx,simpleAuthProvider,new JsonObject());
    }

    static SimpleAuthHandler create(Vertx vertx, SimpleAuthProvider simpleAuthProvider, JsonObject config){
        return new SimpleAuthHandlerImpl(vertx,simpleAuthProvider,config);
    }

    boolean checkAnno(String permission);

    void checkCookie(RoutingContext ctx);

    SimpleAuthHandler addAnnoPermissions(List<String> permissions);

    SimpleAuthHandler addAnnoPermission(String permission);
}
