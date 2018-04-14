package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.config.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.encryption.SimpleAuthEncryption;
import fun.bookish.vertx.auth.simple.exception.AuthenticateFailException;
import fun.bookish.vertx.auth.simple.exception.AuthoriseFailException;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SimpleUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.impl.SessionImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSimpleAuthHandler implements SimpleAuthHandler {

    protected Vertx vertx;
    protected SimpleAuthProvider simpleAuthProvider;
    protected SimpleAuthOptions options;
    protected SecurityManager securityManager;
    protected SimpleAuthEncryption encryption;
    protected PermissionStrategy permissionStrategy;

    protected final Set<String> annoPermissionSet = new HashSet<>();

    AbstractSimpleAuthHandler(Vertx vertx,SimpleAuthProvider simpleAuthProvider,SimpleAuthOptions options){
        this.vertx = vertx;
        this.simpleAuthProvider = simpleAuthProvider;
        this.options = options.copy();
        handleConfig(this.options);
        createSecurityManager(this.vertx,this.simpleAuthProvider,this.encryption,this.options);
    }

    /**
     * 创建安全管理器
     * @param vertx
     * @param simpleAuthProvider
     * @param encryption
     * @param options
     */
    private void createSecurityManager(Vertx vertx, SimpleAuthProvider simpleAuthProvider, SimpleAuthEncryption encryption, SimpleAuthOptions options){

        SecurityManager securityManager = vertx.getOrCreateContext().get(SimpleAuthConstants.VERTX_CTX_SECURITY_MANAGER_KEY);

        if(securityManager == null){
            synchronized (this){
                securityManager = vertx.getOrCreateContext().get(SimpleAuthConstants.VERTX_CTX_SECURITY_MANAGER_KEY);
                if(securityManager == null){
                    SecurityManager newSecurityManager = new SecurityManager(vertx,simpleAuthProvider,this.encryption,options);
                    vertx.getOrCreateContext().put(SimpleAuthConstants.VERTX_CTX_SECURITY_MANAGER_KEY,newSecurityManager);
                    securityManager = newSecurityManager;
                }
            }
        }

        this.securityManager = securityManager;

    }

    /**
     * 解析传入的配置对象
     * @param options
     */
    private void handleConfig(SimpleAuthOptions options) {

        //校验jsessionid cookie key
        String sessionIdCookieKey = options.getJsessionIdCookieKey();
        if(StringUtils.isBlank(sessionIdCookieKey)){
            options.setJsessionIdCookieKey(SimpleAuthConstants.JSESSIONID_COOKIE_KEY);
        }

        //校验rememberme cookie key
        String rememberMeCookieKey = options.getRememberMeCookieKey();
        if(StringUtils.isBlank(rememberMeCookieKey)){
            options.setRememberMeCookieKey(SimpleAuthConstants.REMEMBERME_COOKIE_KEY);
        }

        //校验rememberme key
        String rememberMeKey = options.getRememberMeKey();
        if(StringUtils.isBlank(rememberMeKey)){
            options.setRememberMeKey(SimpleAuthConstants.REMEMBERME_KEY);
        }

        //校验session过期时间
        long sessionTimeout = options.getSessionTimeout();
        if(sessionTimeout <= 0L){
            options.setSessionTimeout(SimpleAuthConstants.DEFAULT_SESSION_TIMEOUT);
        }

        //校验rememberMe cookie过期时间
        long rememberMeTimeout = options.getRememberMeTimeout();
        if(rememberMeTimeout <= 0L){
            options.setRememberMeTimeout(SimpleAuthConstants.DEFAULT_REMEMBERME_TIMEOUT);
        }

        //校验加密方式
        SimpleAuthEncryption encryptType = options.getSimpleEncryption();
        this.encryption = encryptType==null? SimpleAuthConstants.DEFAULT_ENCRYPTION_STRATEGY : encryptType;

        //校验加密密钥
        String encryptionKey = options.getEncryptionKey();
        if(StringUtils.isBlank(encryptionKey)){
            options.setEncryptionKey(SimpleAuthConstants.DEFAULT_ENCRYPT_KEY);
        }

        //校验访问路由校验接口
        PermissionStrategy permissionStrategy = options.getPermissionStrategy();
        this.permissionStrategy = permissionStrategy == null ? SimpleAuthConstants.DEFAULT_PERMISSION_STRATEGY : permissionStrategy;

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
    public final void checkSessionId(RoutingContext ctx) {
        String sessionIdCookieKey = this.options.getJsessionIdCookieKey();
        String sessionIdFromCookie = ctx.getCookie(sessionIdCookieKey).getValue();
        String sessionIdFromParam = ctx.request().getParam(sessionIdCookieKey);
        boolean sessionIdNotExists = StringUtils.isBlank(sessionIdFromCookie) && StringUtils.isBlank(sessionIdFromParam);
        if(sessionIdNotExists){
            String newSessionId = SimpleUtils.getUUID();
            ctx.addCookie(Cookie.cookie(sessionIdCookieKey, newSessionId).setHttpOnly(true).setPath("/"));
            ctx.put(SimpleAuthConstants.JESSIONID_KEY,newSessionId);
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

        //检查session
        checkSessionId(ctx);

        HttpMethod method = ctx.request().method();

        //拼接权限字符串
        String permission = this.permissionStrategy.create(ctx.request());

        if(method == HttpMethod.OPTIONS || checkAnno(permission)){
            ctx.put(SimpleAuthConstants.ROUTER_CTX_START_TIME_KEY,System.nanoTime());
            ctx.next();
        }else{
            //获取当前用户
            Subject subject = this.securityManager.getSubject(ctx);
            if(subject.isAuthenticated()){
                subject.isAuthorised(permission, res -> {
                    if(res.succeeded() && res.result()){
                        //将当前用户信息放入router上下文中,便于子路由获取.(此时用户已登录)
                        ctx.put(SimpleAuthConstants.ROUTER_CTX_SUBJECT_KEY,subject);
                        ctx.put(SimpleAuthConstants.ROUTER_CTX_START_TIME_KEY,System.nanoTime());
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
