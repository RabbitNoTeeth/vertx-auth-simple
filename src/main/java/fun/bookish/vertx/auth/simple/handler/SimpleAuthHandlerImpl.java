package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.configurable.*;
import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SubjectUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.PRNG;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.impl.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleAuthHandlerImpl implements SimpleAuthHandler {

    private final Logger logger = LoggerFactory.getLogger(SimpleAuthHandlerImpl.class);

    private Vertx vertx;
    private SimpleAuthProvider simpleAuthProvider;
    private SimpleAuthOptions options;
    private PermissionStrategy permissionStrategy;
    private SessionIdStrategy sessionIdStrategy;
    private SessionPersistStrategy sessionPersistStrategy;
    private RememberMePersistStrategy rememberMePersistStrategy;
    private RealmStrategy realmStrategy;

    private final Set<String> annoPermissionSet = new HashSet<>();

    SimpleAuthHandlerImpl(Vertx vertx,SimpleAuthProvider simpleAuthProvider,SimpleAuthOptions options){
        this.vertx = vertx;
        options.setVertx(vertx);
        this.simpleAuthProvider = simpleAuthProvider;
        this.options = options;
        this.permissionStrategy = options.getPermissionStrategy();
        this.sessionIdStrategy = options.getSessionIdStrategy();
        this.sessionPersistStrategy = options.getSessionPersistStrategy();
        this.rememberMePersistStrategy = options.getRememberMePersistStrategy();
        this.realmStrategy = options.getRealmStrategy();
        if(options.getAnnoPermissions() != null){
            this.annoPermissionSet.addAll(options.getAnnoPermissions());
        }
    }


    @Override
    public final boolean checkAnno(String requestPermission) {
        return this.annoPermissionSet.contains("*") || this.annoPermissionSet.contains(requestPermission) ||
                this.annoPermissionSet.stream().anyMatch(cachePermission -> permissionStrategy.checkPermission(requestPermission,cachePermission));
    }


    @Override
    public final SimpleAuthHandler addAnnoPermissions(Collection<String> permissions) {
        this.annoPermissionSet.addAll(permissions);
        return this;
    }


    @Override
    public final SimpleAuthHandler addAnnoPermission(String permission) {
        this.annoPermissionSet.add(permission);
        return this;
    }

    @Override
    public final void handle(RoutingContext ctx) {

        String permission = this.permissionStrategy.generatePermission(ctx.request());

        if(ctx.request().method() == HttpMethod.OPTIONS){
            ctx.next();
        }else{
            String sessionId = checkSession(ctx);
            if(checkAnno(permission)){
                logger.info("拦截请求：" + permission + ", 允许匿名：是");
                ctx.next();
            }else{
                Subject subject = SubjectUtil.getSubject(ctx);
                if(subject.isAuthenticated()){
                    subject.isAuthorised(permission, res -> {
                        if(res.succeeded() && res.result()){
                            logger.info("拦截请求：" + permission + ", 允许匿名：否， 当前session：" + sessionId + ", 校验结果：允许访问");
                            realmStrategy.afterAuthorisedSucceed(ctx);
                            ctx.next();
                        }else{
                            logger.info("拦截请求：" + permission + ", 允许匿名：否， 当前session：" + sessionId + ", 校验结果：用户权限不足，不允许访问");
                            realmStrategy.handleAuthorisedFailed(ctx);
                        }
                    });
                }else{
                    logger.info("拦截请求：" + permission + ", 允许匿名：否， 当前session：" + sessionId + "校验结果：用户未登录，不允许访问");
                    realmStrategy.handleAuthenticatedFailed(ctx);
                }
            }
        }
    }

    private String checkSession(RoutingContext ctx) {
        Cookie rememberMe = ctx.getCookie("RememberMe");
        if(rememberMe != null){
            Session session = rememberMePersistStrategy.get(rememberMe);
            if(session != null){
                ctx.setSession(session);
                if(sessionIdStrategy.getSessionId(ctx) == null){
                    sessionIdStrategy.writeSessionId(session.id(),ctx);
                }
                return session.id();
            }
        }

        String sessionId = sessionIdStrategy.getSessionId(ctx);
        Session session;
        if(sessionId == null){
            session = createSession();
            sessionIdStrategy.writeSessionId(session.id(),ctx);
            sessionId = session.id();
        }else{
            session = sessionPersistStrategy.get(sessionId);
            if(session == null){
                session = createSession();
                sessionIdStrategy.writeSessionId(session.id(),ctx);
                sessionId = session.id();
            }
        }
        ctx.setSession(session);
        return sessionId;

    }

    private Session createSession(){
        Session session = new SessionImpl(new PRNG(this.vertx),this.options.getSessionTimeout()*1000,8);
        session.put(SimpleAuthConstants.SESSION_CREATE_TIME_KEY, LocalDateTime.now());
        session.put(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION,new Subject(session.id(),this.vertx,this.simpleAuthProvider,this.options));
        sessionPersistStrategy.cache(session);
        return session;
    }

}
