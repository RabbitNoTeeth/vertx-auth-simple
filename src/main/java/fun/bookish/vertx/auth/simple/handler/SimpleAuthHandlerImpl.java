package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.config.SimpleAuthConfigKey;
import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.encryption.DefaultAESEncryption;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryption;
import fun.bookish.vertx.auth.simple.exception.AuthenticateFailException;
import fun.bookish.vertx.auth.simple.exception.AuthoriseFailException;
import fun.bookish.vertx.auth.simple.ext.DefaultPermissionStrategyImpl;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SimpleUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Don9
 * @create 2018-04-10-14:58
 **/
public class SimpleAuthHandlerImpl extends AbstractSimpleAuthHandler{

    SimpleAuthHandlerImpl(Vertx vertx,SimpleAuthProvider simpleAuthProvider,JsonObject config){
        super(vertx, simpleAuthProvider, config);
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
