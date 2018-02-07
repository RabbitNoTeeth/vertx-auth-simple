package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class Subject {

    private final String id;
    private final AuthProvider authProvider;

    public Subject(String id,AuthProvider authProvider){
        this.id = id;
        this.authProvider = authProvider;
    }

    private final AtomicReference<User> authUserRef = new AtomicReference<>();

    private final AtomicReference<Boolean> rememberMeRef = new AtomicReference<>(false);

    private void setUser(User user){
        this.authUserRef.compareAndSet(null,user);
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
                this.setUser(res.result());
                //判断rememberMe
                Object rememberMe = authInfo.getValue("RememberMe");
                if(rememberMe != null){
                    if(rememberMe.equals(true) || rememberMe.toString().equals("true")){
                        this.rememberMeRef.compareAndSet(false,true);
                        String cookieValue = Base64.getEncoder().encodeToString(authInfo.getString("username").getBytes(Charset.forName("UTF-8")));
                        Cookie cookie = Cookie.cookie(SimpleConstants.COOKIE_REMEMBERME_KEY,cookieValue).setHttpOnly(false).setPath("/");
                        ctx.addCookie(cookie);
                        SecurityManager.cacheRememberMeSubject(cookieValue,this);
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
        User authUser = this.authUserRef.get();
        if(authUser == null){
            resultHandler.handle(Future.succeededFuture(false));
        }else{
            authUser.isAuthorised(authority, resultHandler);
        }
    }

    /**
     * 用户注销
     */
    public void logout(){
        this.authUserRef.set(null);
        this.rememberMeRef.set(false);
    }

    public boolean isAuthenticated(){
        return this.authUserRef.get() != null;
    }

    public User getUser(){
        return this.authUserRef.get();
    }

    public boolean isRememberMe(){
        return this.rememberMeRef.get();
    }

    public JsonObject getPrincipal() {
        User user = this.getUser();
        return user==null?null:user.principal();
    }
}
