package fun.bookish.vertx.auth.simple.provider;

import fun.bookish.vertx.auth.simple.user.SimpleAuthUser;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;

/**
 * 认证接口(需要客户端提供实现)
 */
public interface SimpleAuthProvider extends AuthProvider {

}
