package fun.bookish.vertx.auth.simple.handler;


import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public interface SimpleAuthHandler extends Handler<RoutingContext> {

    static SimpleAuthHandler create(SimpleAuthProvider simpleAuthProvider){
        return new SimpleAuthHandlerImpl(simpleAuthProvider);
    }

    boolean checkAnno(String permission);

    void checkCookie(RoutingContext ctx);

    SimpleAuthHandler addAnnoPermissions(List<String> permissions);

    SimpleAuthHandler addAnnoPermission(String permission);
}
