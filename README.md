# vertx-auth-simple
适用于resuful api的vertx动态权限认证框架

## 配置使用
1.提供SimpleAuthProvider接口实现,自定义用户的授权
下面是一个实现类示例
<pre><code>

class SimpleAuthProviderImpl(private val mongoClient: MongoClient): SimpleAuthProvider {

    override fun authenticate(authInfo: JsonObject, resultHandler: Handler<AsyncResult<User>>) {
        val usernamePswInfo = JsonObject().put("username",authInfo.getString("username"))
                .put("password",authInfo.getString("password"))
        //查询用户
        mongoClient.findOne("SysUser",usernamePswInfo, JsonObject()){ userResult ->
            if(userResult.succeeded()){
                if(userResult.result() !=null ){
                    val userBean = userResult.result().mapTo(SysUser::class.java)

                    //查询用户角色
                    mongoClient.find("SysRole",JsonObject().put("_id",JsonObject().put("\$in",userBean.roles))){ roleResult ->
                        if(roleResult.succeeded()){
                            val roleBeanList = roleResult.result().map { it.mapTo(SysRole::class.java) }.toList()
                            val permissionIdList = mutableListOf<String>()
                            roleBeanList.forEach {
                                permissionIdList.addAll(it.permissions)
                                userBean.roleName += it.name + ";"
                            }

                            //查询用户角色拥有的权限
                            mongoClient.find("SysPermission",JsonObject().put("_id",JsonObject().put("\$in",permissionIdList))){ permissionResult ->
                                if(permissionResult.succeeded()){
                                    val user = SimpleAuthUser()
                                    user.setAuthProvider(this)
                                    user.setPrincipal(JsonObject.mapFrom(userBean).apply { remove("password") })
                                    user.appendPermissions(permissionResult.result().map { it.getString("encoding") }.toList())
                                    resultHandler.handle(Future.succeededFuture(user))
                                }else{
                                    resultHandler.handle(Future.failedFuture(permissionResult.cause()))
                                }
                            }

                        }else{
                            resultHandler.handle(Future.failedFuture(roleResult.cause()))
                        }
                    }
                }else{
                    resultHandler.handle(Future.failedFuture(IllegalArgumentException("用户名或密码错误")))
                }
            }else{
                resultHandler.handle(Future.failedFuture(userResult.cause()))
            }
        }
    }

}

</code></pre>

2.配置router

<pre><code>
// 定义不需要拦截的访问
val annoPermissions = listOf("GET:/articles/page","GET:/articleClassifies/tree")
// 创建实现类
val simpleAuthProviderImpl = SimpleAuthProviderImpl(mongoClient.mongo)
// 注册权限处理器
router.route().handler(SimpleAuthHandler.create(simpleAuthProviderImpl).addAnnoPermissions(annoPermissions))
</code></pre>

3.获取Subject实体<br>
<pre><code>
// 方式一:直接从管理器中获取
val subject = SecurityManager.getSubject(ctx)
// 方式二:从RoterContext上下文中获取(SimpleAuthHandler会在拦截请求后将当前会话的subject放到RoterContext上下文中)
val subject = ctx.get<Subject>(SimpleConstants.CTX_SUBJECT_KEY)
</code></pre>

4.注意<br>
在vertx-auth-simple中,访问权限的字符串形式为 请求方法:请求地址,如 GET:/articles/page <br>
同时,vertx-auth-simple支持最后一位\*号匹配,如 GET:/article/123456 和 GET:/article/456789 分别表示请求不同的资源,那么可以通过 GET:/article/\* 或者 GET:/article 来同时匹配两者 <br>
单纯一个*号表示匹配所有访问<br>







