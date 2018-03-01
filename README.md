# vertx-auth-simple
适用于resuful api的vertx动态权限认证框架

## 配置使用（how to use）
1.提供SimpleAuthProvider接口实现(provide a implement of SimpleAuthProvider)
<pre><code>

public class MyAuthProviderImpl implements SimpleAuthProvider {

    /**
     * 
     * @param authInfo 用户登录时提交的验证信息
     * @param resultHandler 处理用户验证结果
     *                      验证成功，创建SimpleAuthUser对象（该对象中包含了用户权限信息）交由resultHandler处理
     *                      验证失败，自定义包含失败原因的异常，交由resultHandler处理
    */
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        // do your authentication...
    }

}

</code></pre>

<br>
2.配置router（config the Router）

<pre><code>
// 定义不需要拦截的访问
List<String> annoPermissions = listOf("GET:/articles/page","GET:/articleClassifies/tree")

// 创建实现类
MyAuthProviderImpl myAuthProviderImpl = new myAuthProviderImpl()

// 注册权限处理器
router.route().handler(SimpleAuthHandler.create(this.vertx,myAuthProviderImpl).addAnnoPermissions(annoPermissions))
</code></pre>

<br>
3.获取Subject实体<br>（get the subject of user）

<pre><code>
//从RoterContext上下文中获取(SimpleAuthHandler会在拦截请求后将当前会话的subject放到RoterContext上下文中)
Subject subject = ctx.get(SimpleConstants.CTX_SUBJECT_KEY)
</code></pre>

<br>
4.注意<br>
在vertx-auth-simple中,默认的权限字符串生成格式为 "请求方法:请求地址",如 "GET:/articles/page" <br>
默认的权限字符串校验支持最后一位*号匹配,如 GET:/article/123456 和 GET:/article/456789 分别表示请求不同的资源,那么可以通过 GET:/article/* 或者 GET:/article 来同时匹配两者 <br>
单纯一个*号表示匹配所有访问<br>

<br>
5.自定义权限字符串的生成格式和校验规则<br>

<pre><code>
提供PermissionStrategy接口的实现
public class MyPermissionStrategy implements PermissionStrategy{

    @Override
    public String create(HttpServerRequest request){
        //自定义权限字符串生成格式并返回
    }
    
    @Override
    public boolean match(String requestPermission,String cachedPermission){
        //自定义权限字符串的校验规则
    }
}

创建权限拦截器时，传入自定义的权限字符串策略
SimpleAuthHandler handler = SimpleAuthHandler.create(this.vertx,myAuthProviderImpl);
</code></pre>







