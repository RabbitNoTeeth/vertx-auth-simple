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
// 创建配置类
SimpleAuthOptions options = new SimpleAuthOptions();
// 定义不需要拦截的访问
List<String> annoPermissions = listOf("GET:/articles/page","GET:/articleClassifies/tree")
options.setAnnoPermissions(annoPermissions);

// 创建并注册权限处理器
SimpleAuthHandler authHandler = SimpleAuthHandler.create(this.vertx,new myAuthProviderImpl(),options);

router.route().handler(authHandler);
</code></pre>

<br><br><br>
3.获取Subject实体（get the subject of user）<br>

<pre><code>
Subject subject = SubjectUtil.getSubject(routingContext);
</code></pre>

<br><br><br>
4.注意<br>
在vertx-auth-simple中,默认的权限字符串生成格式为 "请求方法:请求地址",如 "GET:/articles/page" <br>
默认的权限字符串校验支持最后一位\*号匹配,如 GET:/article/123456 和 GET:/article/456789 分别表示请求不同的资源,
那么可以通过 GET:/article/* 或者 GET:/article 来同时匹配两者 <br>单纯一个*号表示匹配所有访问<br>

<br><br><br>
5.扩展功能<br>
下面所有扩展接口在vertx-auth-simple中都提供了默认实现（可零配置开箱即用），可以根据场景需要来自定义各个接口实现

1> 实现PermissionStrategy接口，自定义权限字符串的生成格式和校验规则

<pre><code>
SimpleAuthOptions options = new SimpleAuthOptions();
options.setPermissionStrategy(new MyPermissionStrategyImpl());
</code></pre>

2> 实现SessionIdStrategy接口，自定义sessionId处理策略

<pre><code>
 SimpleAuthOptions options = new SimpleAuthOptions();
 options.setSessionIdStrategy(new MySessionIdStrategyImpl());
</code></pre>

3> 实现SessionPersistStrategy接口，自定义session持久化方式

<pre><code>
 SimpleAuthOptions options = new SimpleAuthOptions();
 options.setSessionPersistStrategy(new MySessionPersistStrategyImpl());
</code></pre>

4> 实现RememberMePersistStrategy接口，自定义rememberMe信息的持久化方式

<pre><code>
 SimpleAuthOptions options = new SimpleAuthOptions();
 options.setRememberMePersistStrategy(new MyRememberMePersistStrategyImpl());
</code></pre>

5> 实现RealmStrategy接口，自定义权限验证成功或者失败后的处理动作
<pre><code>
  SimpleAuthOptions options = new SimpleAuthOptions();
  options.setRealmStrategy(new MyRealmStrategyImpl());
</code></pre>




