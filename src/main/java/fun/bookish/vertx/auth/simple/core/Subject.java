package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.constant.SimpleConfigConstants;
import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryptMode;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryption;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.user.SimpleAuthUser;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;

/**
 *
 */
public class Subject {

    public final LocalDateTime time = LocalDateTime.now();

    private final SimpleAuthProvider authProvider;
    private final SimpleEncryption encryption;
    private final JsonObject config;

    public Subject(SimpleAuthProvider authProvider,SimpleEncryption encryption,JsonObject config){
        this.authProvider = authProvider;
        this.encryption = encryption;
        this.config = config;
    }

    private volatile User authUser;

    private volatile boolean rememberMe;

    public void setUser(User user){
        this.authUser = user;
    }

    /**
     * 用户登录
     * @param ctx
     * @param authInfo
     * @param resultHandler
     */
    public void login(RoutingContext ctx, JsonObject authInfo, Handler<AsyncResult<Void>> resultHandler){
        authProvider.authenticate(authInfo,res -> {
            if(res.succeeded()){

                SimpleAuthUser user = res.result();
                this.setUser(user);

                //判断rememberMe
                Object rememberMe = authInfo.getValue(SimpleConstants.AUTH_REMEMBERME_KEY);
                if(rememberMe != null){
                    if(rememberMe.equals(true) || rememberMe.toString().equals("true")){
                        this.rememberMe = true;
                        //对user中的principle进行加密
                        String cookieValue = this.encryption.encryptOrDecrypt(user.principal().toString(),SimpleConstants.DEFAULT_AES_KEY
                                , SimpleEncryptMode.ENCRYPT);
                        //创建rememberMe cookie，并写入到response中
                        Cookie cookie = Cookie.cookie(SimpleConstants.COOKIE_REMEMBERME_KEY,cookieValue)
                                .setMaxAge(this.config.getInteger(SimpleConfigConstants.REMEMBERME_TIMEOUT))
                                .setHttpOnly(false).setPath("/");
                        ctx.addCookie(cookie);
                    }
                }

                resultHandler.handle(Future.succeededFuture());
            }else{
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    /**
     * 验证用户权限
     * @param authority
     * @param resultHandler
     */
    public void isAuthorised(String authority, Handler<AsyncResult<Boolean>> resultHandler){
        if(this.authUser == null){
            resultHandler.handle(Future.succeededFuture(false));
        }else{
            authUser.isAuthorised(authority, resultHandler);
        }
    }

    /**
     * 用户注销
     */
    public void logout(RoutingContext ctx){
        this.authUser = null;
        this.rememberMe = false;
        SecurityManager securityManager = ctx.vertx().getOrCreateContext().get(SimpleConstants.VERTX_CTX_SECURITY_MANAGER_KEY);
        securityManager.remove(ctx);
    }

    public boolean isAuthenticated(){
        return this.authUser != null;
    }

    public User getUser(){
        return this.authUser;
    }

    public boolean isRememberMe(){
        return this.rememberMe;
    }

    public Subject setRememberMe(){
        this.rememberMe = true;
        return this;
    }

    public JsonObject getPrincipal() {
        return this.authUser==null?null : this.authUser.principal();
    }
}
