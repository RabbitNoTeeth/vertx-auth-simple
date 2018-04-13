# vertx-auth-simple
基于vertx-auth-common和resuful api的vertx动态权限认证框架<br>
适用场景：用户登陆后动态从数据库或其他介质中获取权限信息

## 配置使用（how to use）

<br><br><br>
1.提供SimpleAuthProvider接口实现(provide a implement of SimpleAuthProvider)

<pre><code>

public class MyAuthProviderImpl implements SimpleAuthProvider {

    /**
     * 
     * @param authInfo 用户登录时提交的验证信息
     * @param resultHandler 处理用户验证结果
     *                      验证成功，创建SimpleAuthUser对象（该对象中包含用户权限信息）交由resultHandler处理
     *                      验证失败，自定义包含失败原因的异常，交由resultHandler处理
    */
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        /** for example：
        if(authInfo is right){
            SimpleAuthUser user = new SimpleAuthUser();
            user.setAuthProvider(this);
            user.setPrincipal(...);
            user.appendPermissions(...);
            resultHandler.handle(Future.succeededFuture(user));
        }else{
            resultHandler.handle(Future.failedFuture(a Throwable));
        } 
        */
    }

}

</code></pre>

<br><br><br>
2.配置router（config the Router）

<pre><code>
// 定义不需要拦截的访问
List<String> annoPermissions = listOf("GET:/articles/page","GET:/articleClassifies/tree")

// 创建实现类
MyAuthProviderImpl myAuthProviderImpl = new myAuthProviderImpl()

// 创建并注册权限处理器
SimpleAuthHandler authHandler = SimpleAuthHandler.create(this.vertx,myAuthProviderImpl);
authHandler.addAnnoPermissions(annoPermissions)
router.route().handler(authHandler);
</code></pre>

<br><br><br>
3.获取Subject实体（get the subject of user）<br>

<pre><code>
//从RoterContext上下文中获取(SimpleAuthHandler会在拦截请求后将当前会话的subject放到RoterContext上下文中)
Subject subject = ctx.get(SimpleConstants.CTX_SUBJECT_KEY)
</code></pre>

<br><br><br>
4.注意<br>
在vertx-auth-simple中,默认的权限字符串生成格式为 "请求方法:请求地址",如 "GET:/articles/page" <br>
默认的权限字符串校验支持最后一位\*号匹配,如 GET:/article/123456 和 GET:/article/456789 分别表示请求不同的资源,
那么可以通过 GET:/article/* 或者 GET:/article 来同时匹配两者 <br>单纯一个*号表示匹配所有访问<br>

<br><br><br>
5.扩展功能<br>

1> 自定义权限字符串的生成格式和校验规则

<pre><code>
//首先，提供PermissionStrategy接口的实现
public class MyPermissionStrategy implements PermissionStrategy{
    @Override
    public String create(HttpServerRequest request){ //自定义权限字符串生成格式并返回 }
    @Override
    public boolean match(String requestPermission,String cachedPermission){ //自定义权限字符串的校验规则 }
}
SimpleAuthOptions options = new SimpleAuthOptions();
options.setPermissionStrategy(new MyPermissionStrategy());
//然后在创建权限拦截器时，传入自定义的权限字符串策略
SimpleAuthHandler authHandler = SimpleAuthHandler.create(this.vertx,myAuthProviderImpl,options);
</code></pre>

2> 自定义session会话过期时间、rememberMe cookie的过期时间和加密密钥

<pre><code>
 SimpleAuthOptions options = new SimpleAuthOptions();
 options.setSessionTimeout(3600*30); //时间单位为s
 options.setRememberMeTimeout(3600*24*30);  //时间单位为s
 options.setEncryptionKey("自定义的rememberme cookie加密密钥");
 SimpleAuthHandler authHandler = SimpleAuthHandler.create(this.vertx,myAuthProviderImpl,options);
</code></pre>

3> 自定义rememberMe cookie加密方式

<pre><code>
 
 public class MySimpleAuthEncryption implements SimpleAuthEncryption{
    @Override
    public String encryptOrDecrypt(String data,String key,SimpleAuthEncryptMode mode){
        //...
    }
 }
 
 SimpleAuthOptions options = new SimpleAuthOptions();
 options.setSimpleEncryption(new MySimpleAuthEncryption());
 SimpleAuthHandler authHandler = SimpleAuthHandler.create(this.vertx,myAuthProviderImpl,options);
</code></pre>





