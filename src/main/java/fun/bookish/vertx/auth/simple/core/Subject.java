package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.configurable.RememberMePersistStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class Subject {

    private String sessionId;
    private Vertx vertx;
    private SimpleAuthProvider authProvider;
    private SessionPersistStrategy sessionPersistStrategy;
    private SessionIdStrategy sessionIdStrategy;
    private RememberMePersistStrategy rememberMePersistStrategy;
    private Long rememberMeTimeout;

    public Subject(){}

    public Subject(String sessionID, Vertx vertx, SimpleAuthProvider authProvider, SimpleAuthOptions options){
        this.sessionId = sessionID;
        this.vertx = vertx;
        this.authProvider = authProvider;
        this.sessionPersistStrategy = options.getSessionPersistStrategy();
        this.sessionIdStrategy = options.getSessionIdStrategy();
        this.rememberMePersistStrategy = options.getRememberMePersistStrategy();
        this.rememberMeTimeout = options.getRememberMeTimeout();
    }

    private AtomicReference<User> authUserRef = new AtomicReference<>();

    private AtomicBoolean rememberMeRef = new AtomicBoolean(false);

    /**
     * 用户登录
     */
    public void login(RoutingContext ctx, JsonObject authInfo, Handler<AsyncResult<Void>> resultHandler){
        this.vertx.executeBlocking(future -> authProvider.authenticate(authInfo, res -> {
            if(res.succeeded()){
                this.authUserRef.set(res.result());
                ctx.session().put(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION,this);
                try{
                    //持久化session
                    sessionPersistStrategy.cache(ctx.session());
                    //判断是否需要rememberMe
                    if(this.rememberMeRef.get()){
                        Cookie cookie = Cookie.cookie("RememberMe",ctx.session().id())
                                                .setMaxAge(rememberMeTimeout)
                                                .setHttpOnly(false).setPath("/");
                        rememberMePersistStrategy.cache(cookie,ctx.session());
                        ctx.addCookie(cookie);
                    }
                }catch (Exception e){
                    future.fail(e);
                }
                future.complete();
            }else{
                future.fail(res.cause());
            }
        }),resultHandler);
    }

    /**
     * 验证用户权限
     */
    public void isAuthorised(String authority, Handler<AsyncResult<Boolean>> resultHandler){
        if(this.authUserRef.get() == null){
            resultHandler.handle(Future.succeededFuture(false));
        }else{
            authUserRef.get().isAuthorised(authority, resultHandler);
        }
    }

    /**
     * 用户注销
     */
    public void logout(RoutingContext ctx){

        this.authUserRef.set(null);
        ctx.setUser(null);
        sessionPersistStrategy.remove(ctx.session());
        Cookie rememberMeCookie = ctx.removeCookie("RememberMe");
        if(rememberMeCookie != null){
            rememberMePersistStrategy.remove(rememberMeCookie);
        }

    }

    public boolean isAuthenticated(){
        return this.authUserRef.get() != null;
    }

    public User getUser(){
        return this.authUserRef.get();
    }

    public JsonObject getPrincipal() {
        return this.authUserRef.get()==null? null : this.authUserRef.get().principal();
    }

    public Subject enableRememberMe(boolean state){
        this.rememberMeRef.set(state);
        return this;
    }

}
