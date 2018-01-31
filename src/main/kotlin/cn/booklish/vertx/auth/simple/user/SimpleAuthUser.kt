package cn.booklish.vertx.auth.simple.user

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider
import java.util.concurrent.atomic.AtomicReference


class SimpleAuthUser : AbstractUser() {

    private var authProvider:AtomicReference<AuthProvider> = AtomicReference()

    private var principal:AtomicReference<JsonObject> = AtomicReference()

    override fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>) {
        val status = if(this.cachedPermissions.contains("*")){
            true
        }else{
            this.cachedPermissions.any { permission.startsWith(it.replace("*",""),false) }
        }
        resultHandler.handle(Future.succeededFuture(status))
    }

    override fun setAuthProvider(authProvider: AuthProvider) {
        this.authProvider.compareAndSet(null,authProvider)
    }

    override fun principal(): JsonObject {
        return this.principal.get()
    }

    fun setPrincipal(principal: JsonObject){
        this.principal.compareAndSet(null,principal)
    }

    fun appendPermission(permission: String){
        this.cachedPermissions.add(permission)
    }

    fun appendPermissions(permissions: Collection<String>){
        this.cachedPermissions.addAll(permissions)
    }


}