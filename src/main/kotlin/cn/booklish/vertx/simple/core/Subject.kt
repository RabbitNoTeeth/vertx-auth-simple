package cn.booklish.vertx.simple.core

import cn.booklish.vertx.simple.manager.SecurityManager
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import java.util.*
import java.util.concurrent.atomic.AtomicReference


class Subject(private val authProvider: AuthProvider) {

    private var authUser: AtomicReference<User> = AtomicReference()

    private val rememberMe:AtomicReference<Boolean> = AtomicReference(false)

    fun isAuthenticated():Boolean {
        return authUser.get() != null
    }

    private fun setUser(user:User){
        this.authUser.compareAndSet(null,user)
    }

    fun getUser():User?{
        return this.authUser.get()
    }

    fun login(ctx: RoutingContext, authInfo:JsonObject, resultHandler:Handler<AsyncResult<Void>>){
        authProvider.authenticate(authInfo){
            if(it.succeeded()){
                this.setUser(it.result())

                //判断rememberMe
                val rememberMe = authInfo.getValue("RememberMe")
                if(rememberMe != null){
                    val booleanType = (rememberMe is Boolean) && rememberMe
                    val stringType = (rememberMe is String) && rememberMe.equals("true",true)
                    if(booleanType || stringType){
                        this.rememberMe.compareAndSet(false,true)
                        val cookieValue = Base64.getEncoder().encodeToString(authInfo.getString("username").toByteArray(Charsets.UTF_8))
                        val cookie = Cookie.cookie("simple-auth.RememberMe",cookieValue).setHttpOnly(false).setPath("/")
                        ctx.addCookie(cookie)
                        SecurityManager.cacheRememberMeSubject(cookieValue,this)
                    }
                }

                resultHandler.handle(Future.succeededFuture())
            }else{
                resultHandler.handle(Future.failedFuture(it.cause()))
            }
        }
    }

    fun logout(){
        this.authUser.set(null)
        this.rememberMe.set(false)
    }

    fun isAuthorised(authority:String, resultHandler:Handler<AsyncResult<Boolean>>){
        val authUser = this.authUser.get()
        if(authUser == null){
            resultHandler.handle(Future.succeededFuture(false))
        }else{
            authUser.isAuthorised(authority, resultHandler)
        }
    }

    fun isRememberMe():Boolean{
        return this.rememberMe.get()
    }

    fun getPrincipal():JsonObject{
        return this.getUser()?.principal()?: JsonObject()
    }

}