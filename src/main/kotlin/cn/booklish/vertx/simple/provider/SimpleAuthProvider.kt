package cn.booklish.vertx.simple.provider

import io.vertx.core.AsyncResult
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User


interface SimpleAuthProvider:AuthProvider {
    fun doAuthenticate(authInfo: JsonObject): AsyncResult<User>
}