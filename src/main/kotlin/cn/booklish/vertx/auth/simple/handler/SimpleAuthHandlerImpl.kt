package cn.booklish.vertx.auth.simple.handler

import cn.booklish.vertx.auth.simple.constant.SimpleConstants
import cn.booklish.vertx.auth.simple.manager.SecurityManager
import cn.booklish.vertx.auth.simple.util.getUuid
import cn.booklish.vertx.auth.simple.provider.SimpleAuthProvider
import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext


class SimpleAuthHandlerImpl(simpleAuthProvider: SimpleAuthProvider): SimpleAuthHandler {

    private val annoPermissionList = mutableListOf<String>()

    init {
        //初始化安全管理器
        SecurityManager.initAuthProvider(simpleAuthProvider)
    }

    /**
     * 添加匿名访问许可
     */
    override fun addAnnoPermissions(permissions:List<String>): SimpleAuthHandlerImpl {
        this.annoPermissionList.addAll(permissions)
        return this
    }

    /**
     * 添加匿名访问许可
     */
    override fun addAnnoPermission(permission:String): SimpleAuthHandlerImpl {
        this.annoPermissionList.add(permission)
        return this
    }

    /**
     * 验证请求资源是否支持匿名访问
     */
    override fun checkAnno(permission: String): Boolean{
        return when{
            this.annoPermissionList.contains("*") -> true
            this.annoPermissionList.contains(permission) -> true
            else -> this.annoPermissionList.any { permission.startsWith(it.replace("*",""),false) }
        }
    }

    /**
     * 检查JSESSIONID cookie,如果请求中没有该cookie,则创建JSESSIONID cookie写入到响应中
     */
    override fun checkCookie(ctx: RoutingContext){
        val jSessionIdCookie = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY)
        if(jSessionIdCookie == null){
            val id = getUuid()
            ctx.addCookie(Cookie.cookie(SimpleConstants.COOKIE_JSESSIONID_KEY,id).setHttpOnly(true).setPath("/"))
        }
    }

    override fun handle(ctx: RoutingContext) {

        //先检查 JSESSIONID cookie
        checkCookie(ctx)

        val method = ctx.request().method()

        //拼接权限字符串
        val permission = "${ctx.request().method().name}:${ctx.request().path()}"

        //获取当前用户
        val subject = SecurityManager.getSubject(ctx)

        if(method == HttpMethod.OPTIONS || checkAnno(permission)){
            //将当前用户信息放入router上下文中,便于子路由获取.(此时用户可能未登录,也可能已登录)
            ctx.put(SimpleConstants.CTX_SUBJECT_KEY,subject)
            ctx.put(SimpleConstants.CTX_START_TIME_KEY,System.nanoTime())
            ctx.next()
        }else{
            if(subject.isAuthenticated()){
                subject.isAuthorised(permission, Handler {
                    if(it.succeeded() && it.result()){
                        //将当前用户信息放入router上下文中,便于子路由获取.(此时用户已登录)
                        ctx.put("subject",subject)
                        ctx.next()
                    }else{
                        ctx.response().setStatusCode(403).end("you have no permission")
                    }
                })
            }else{
                ctx.response().setStatusCode(403).end("you need login first")
            }
        }

    }

}