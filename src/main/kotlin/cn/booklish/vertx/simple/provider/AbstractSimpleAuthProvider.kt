package cn.booklish.vertx.simple.provider

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User


abstract class AbstractSimpleAuthProvider:AuthProvider,SimpleAuthProvider {

    override fun authenticate(authInfo: JsonObject, resultHandler: Handler<AsyncResult<User>>) {
            resultHandler.handle(doAuthenticate(authInfo))
    }

}