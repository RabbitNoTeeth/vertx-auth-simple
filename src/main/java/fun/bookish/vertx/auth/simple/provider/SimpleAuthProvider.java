package fun.bookish.vertx.auth.simple.provider;

import fun.bookish.vertx.auth.simple.user.SimpleAuthUser;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * 认证接口(需要客户端提供实现)
 */
public interface SimpleAuthProvider {

    void authenticate(JsonObject authInfo, Handler<AsyncResult<SimpleAuthUser>> resultHandler);

}
