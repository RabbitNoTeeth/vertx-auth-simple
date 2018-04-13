package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.config.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.exception.AuthenticateFailException;
import fun.bookish.vertx.auth.simple.exception.AuthoriseFailException;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Don9
 * @create 2018-04-10-14:58
 **/
public class SimpleAuthHandlerImpl extends AbstractSimpleAuthHandler{

    SimpleAuthHandlerImpl(Vertx vertx,SimpleAuthProvider simpleAuthProvider,SimpleAuthOptions options){
        super(vertx, simpleAuthProvider, options);
    }

    @Override
    final void handle(AsyncResult<Boolean> result, RoutingContext ctx) {
        if(result.failed()){
            Throwable cause = result.cause();
            if(cause instanceof AuthoriseFailException){
                ctx.response().setStatusCode(403).end("you have no permission to access '"+ctx.request().path()+"'");
            }else if(cause instanceof AuthenticateFailException){
                ctx.response().setStatusCode(403).end("you need login first");
            }
        }
    }
}
