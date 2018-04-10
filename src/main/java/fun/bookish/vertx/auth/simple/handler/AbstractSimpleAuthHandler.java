package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.config.SimpleAuthConfigKey;
import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.encryption.DefaultAESEncryption;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryption;
import fun.bookish.vertx.auth.simple.exception.AuthenticateFailException;
import fun.bookish.vertx.auth.simple.exception.AuthoriseFailException;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;
import fun.bookish.vertx.auth.simple.ext.DefaultPermissionStrategyImpl;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SimpleUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSimpleAuthHandler implements SimpleAuthHandler {

    protected Vertx vertx;
    protected SimpleAuthProvider simpleAuthProvider;
    protected JsonObject config;
    protected SecurityManager securityManager;
    protected SimpleEncryption encryption;
    protected PermissionStrategy permissionStrategy;

    protected final Set<String> annoPermissionSet = new HashSet<>();

    AbstractSimpleAuthHandler(Vertx vertx,SimpleAuthProvider simpleAuthProvider,JsonObject config){
        this.vertx = vertx;
        this.simpleAuthProvider = simpleAuthProvider;
        this.config = new JsonObject(config.getMap());
        handleConfig(this.config);
        this.securityManager = new SecurityManager(vertx,simpleAuthProvider,this.encryption,config);
        vertx.getOrCreateContext().put(SimpleConstants.VERTX_CTX_SECURITY_MANAGER_KEY,this.securityManager);
    }

    // 解析传入的配置对象
    private final void handleConfig(JsonObject config) {

        //配置session过期时间
        Long sessionTimeout = config.getLong(SimpleAuthConfigKey.SESSION_TIMEOUT.value());
        if(sessionTimeout == null || sessionTimeout< SimpleConstants.DEFAULT_SESSION_TIMEOUT){
            config.put(SimpleAuthConfigKey.SESSION_TIMEOUT.value(),SimpleConstants.DEFAULT_SESSION_TIMEOUT);
        }

        //配置rememberMe cookie过期时间
        Long rememberMeTimeout = config.getLong(SimpleAuthConfigKey.REMEMBERME_COOKIE_TIMEOUT.value());
        if(rememberMeTimeout == null || rememberMeTimeout<SimpleConstants.DEFAULT_REMEMBERME_TIMEOUT){
            config.put(SimpleAuthConfigKey.REMEMBERME_COOKIE_TIMEOUT.value(),SimpleConstants.DEFAULT_REMEMBERME_TIMEOUT);
        }

        //配置加密方式
        Object encryptType = config.getValue(SimpleAuthConfigKey.ENCRYPT_TYPE.value());
        if(encryptType!=null && encryptType instanceof SimpleEncryption){
            this.encryption = (SimpleEncryption) encryptType;
        }else {
            this.encryption = new DefaultAESEncryption();
        }

        //配置加密密钥
        String aesKey = config.getString(SimpleAuthConfigKey.ENCRYPT_KEY.value());
        if(StringUtils.isBlank(aesKey)){
            config.put(SimpleAuthConfigKey.ENCRYPT_KEY.value(),SimpleConstants.DEFAULT_AES_KEY);
        }

        //配置访问路由校验接口
        Object permissionStrategy = config.getValue(SimpleAuthConfigKey.PERMISSION_STRATEGY.value());
        if(permissionStrategy!=null && permissionStrategy instanceof PermissionStrategy){
            this.permissionStrategy = (PermissionStrategy) permissionStrategy;
        }else{
            this.permissionStrategy = new DefaultPermissionStrategyImpl();
        }

    }

    /**
     * 验证请求资源是否支持匿名访问
     */
    @Override
    public final boolean checkAnno(String permission) {
        return this.annoPermissionSet.contains("*") || this.annoPermissionSet.contains(permission) ||
                this.annoPermissionSet.stream().anyMatch(per -> permission.startsWith(per.replaceAll("\\*", "")));
    }

    /**
     * 检查JSESSIONID cookie,如果请求中没有该cookie,则创建JSESSIONID cookie写入到响应中
     */
    @Override
    public final void checkCookie(RoutingContext ctx) {
        if(ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY) == null){
            ctx.addCookie(Cookie.cookie(SimpleConstants.COOKIE_JSESSIONID_KEY, SimpleUtils.getUUID()).setHttpOnly(true).setPath("/"));
        }
    }

    /**
     * 添加匿名访问许可
     */
    @Override
    public final SimpleAuthHandler addAnnoPermissions(Collection<String> permissions) {
        this.annoPermissionSet.addAll(permissions);
        return this;
    }

    /**
     * 添加匿名访问许可
     */
    @Override
    public final SimpleAuthHandler addAnnoPermission(String permission) {
        this.annoPermissionSet.add(permission);
        return this;
    }

    @Override
    public final void handle(RoutingContext ctx) {

        //检查JSESSIONID cookie,如果没有，那么生成JSESSIONID cookie并写入到response中
        checkCookie(ctx);

        HttpMethod method = ctx.request().method();

        //拼接权限字符串
        String permission = this.permissionStrategy.create(ctx.request());

        if(method == HttpMethod.OPTIONS || checkAnno(permission)){
            ctx.put(SimpleConstants.ROUTER_CTX_START_TIME_KEY,System.nanoTime());
            ctx.next();
        }else{
            //获取当前用户
            Subject subject = this.securityManager.getSubject(ctx);
            if(subject.isAuthenticated()){
                subject.isAuthorised(permission, res -> {
                    if(res.succeeded() && res.result()){
                        //将当前用户信息放入router上下文中,便于子路由获取.(此时用户已登录)
                        ctx.put(SimpleConstants.ROUTER_CTX_SUBJECT_KEY,subject);
                        ctx.put(SimpleConstants.ROUTER_CTX_START_TIME_KEY,System.nanoTime());
                        ctx.next();
                    }else{
                        handle(Future.failedFuture(new AuthoriseFailException()),ctx);
                    }
                });
            }else{
                handle(Future.failedFuture(new AuthenticateFailException()),ctx);
            }
        }
    }

    abstract void handle(AsyncResult<Boolean> result,RoutingContext ctx);
}
