package fun.bookish.vertx.auth.simple.core;

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
import io.vertx.ext.web.RoutingContext;



public class Subject {

    private String sessionId;
    private Vertx vertx;
    private SimpleAuthProvider authProvider;
    private SessionPersistStrategy sessionPersistStrategy;
    private SessionIdStrategy sessionIdStrategy;

    public Subject(){}

    public Subject(String sessionID, Vertx vertx, SimpleAuthProvider authProvider, SimpleAuthOptions options){
        this.sessionId = sessionID;
        this.vertx = vertx;
        this.authProvider = authProvider;
        this.sessionPersistStrategy = options.getSessionPersistStrategy();
        this.sessionIdStrategy = options.getSessionIdStrategy();
    }

    private volatile User authUser;

    /**
     * 用户登录
     * @param ctx router上下文
     * @param authInfo 用户信息
     * @param resultHandler
     */
    public void login(RoutingContext ctx, JsonObject authInfo, Handler<AsyncResult<Void>> resultHandler){
        this.vertx.executeBlocking(future -> authProvider.authenticate(authInfo, res -> {
            if(res.succeeded()){
                this.authUser = res.result();
                ctx.session().put(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION,this);
                try{
                    //持久化session
                    sessionPersistStrategy.cache(ctx.session());
                    //将sessionId写回到ctx中，具体如何操作由开发者实现
                    sessionIdStrategy.writeSessionId(ctx.session().id(),ctx);
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
        sessionPersistStrategy.remove(ctx.session());
        ctx.session().remove(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION);
        ctx.setSession(null);
        ctx.setUser(null);
    }

    public boolean isAuthenticated(){
        return this.authUser != null;
    }

    public User getUser(){
        return this.authUser;
    }

    public JsonObject getPrincipal() {
        return this.authUser==null? null : this.authUser.principal();
    }

}
