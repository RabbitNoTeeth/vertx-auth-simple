package cn.booklish.vertx.simple.handler

import cn.booklish.vertx.simple.provider.SimpleAuthProvider
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

interface SimpleAuthHandler: Handler<RoutingContext> {

    companion object {
        fun create(simpleAuthProvider: SimpleAuthProvider):SimpleAuthHandler{
            return SimpleAuthHandlerImpl(simpleAuthProvider)
        }
    }

    fun checkAnno(permission: String): Boolean

    fun checkCookie(ctx: RoutingContext)

    fun addAnnoPermissions(permissions:List<String>): SimpleAuthHandler

    fun addAnnoPermission(permission:String): SimpleAuthHandler

}