package fun.bookish.vertx.auth.simple.handler;


import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import java.util.Collection;

public interface SimpleAuthHandler extends Handler<RoutingContext> {

    static SimpleAuthHandler create(Vertx vertx, SimpleAuthProvider simpleAuthProvider){
        return new SimpleAuthHandlerImpl(vertx,simpleAuthProvider,new SimpleAuthOptions());
    }

    static SimpleAuthHandler create(Vertx vertx, SimpleAuthProvider simpleAuthProvider, SimpleAuthOptions options){
        return new SimpleAuthHandlerImpl(vertx,simpleAuthProvider,options);
    }

    boolean checkAnno(String permission);

    SimpleAuthHandler addAnnoPermissions(Collection<String> permissions);

    SimpleAuthHandler addAnnoPermission(String permission);

}
