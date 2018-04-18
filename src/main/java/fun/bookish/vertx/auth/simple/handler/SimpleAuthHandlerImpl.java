package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.configurable.RealmStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SubjectUtil;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.PRNG;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.impl.SessionImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleAuthHandlerImpl implements SimpleAuthHandler {

    private Vertx vertx;
    private SimpleAuthProvider simpleAuthProvider;
    private SimpleAuthOptions options;
    private PermissionStrategy permissionStrategy;
    private SessionIdStrategy sessionIdStrategy;
    private SessionPersistStrategy sessionPersistStrategy;
    private RealmStrategy realmStrategy;

    private final Set<String> annoPermissionSet = new HashSet<>();

    SimpleAuthHandlerImpl(Vertx vertx,SimpleAuthProvider simpleAuthProvider,SimpleAuthOptions options){
        this.vertx = vertx;
        this.simpleAuthProvider = simpleAuthProvider;
        this.options = options;
        this.permissionStrategy = options.getPermissionStrategy();
        this.sessionIdStrategy = options.getSessionIdStrategy();
        this.sessionPersistStrategy = options.getSessionPersistStrategy();
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

        String sessionId = sessionIdStrategy.getSessionId(ctx);
        Session session = sessionPersistStrategy.get(sessionId);
        if(session == null){
            Session newSession = new SessionImpl(new PRNG(this.vertx),1200000L,8);
            newSession.put(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION,new Subject(newSession.id(),this.vertx,this.simpleAuthProvider,this.options));
            sessionPersistStrategy.cache(newSession);
            session = newSession;
        }
        ctx.setSession(session);

        String permission = this.permissionStrategy.generatePermission(ctx.request());

        if(ctx.request().method() == HttpMethod.OPTIONS || checkAnno(permission)){
            ctx.next();
        }else{
            Subject subject = SubjectUtil.getSubject(ctx);
            if(subject.isAuthenticated()){
                subject.isAuthorised(permission, res -> {
                    if(res.succeeded() && res.result()){
                        realmStrategy.afterAuthorisedSucceed(ctx);
                        ctx.next();
                    }else{
                        realmStrategy.handleAuthorisedFailed(ctx);
                    }
                });
            }else{
                realmStrategy.handleAuthenticatedFailed(ctx);
            }
        }
    }

}
