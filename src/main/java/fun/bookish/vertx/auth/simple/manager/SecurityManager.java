package fun.bookish.vertx.auth.simple.manager;

import fun.bookish.vertx.auth.simple.config.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.encryption.SimpleAuthEncryption;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 安全管理器,用户获取当前subject实体
 */
public class SecurityManager {

    private final ConcurrentHashMap<String, Subject> subjectCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Subject> rememberMeCache = new ConcurrentHashMap<>();

    private final Vertx vertx;
    private final SimpleAuthProvider authProvider;
    private final SimpleAuthEncryption encryption;
    private final SimpleAuthOptions options;

    private final String SESSIONID_COOKIE_KEY;
    private final String REMEMBERME_COOKIE_KEY;

    public SecurityManager(Vertx vertx, SimpleAuthProvider authProvider, SimpleAuthEncryption encryption, SimpleAuthOptions options){
        this.vertx = vertx;
        this.authProvider = authProvider;
        this.encryption = encryption;
        this.options = options;
        this.SESSIONID_COOKIE_KEY = options.getJsessionIdCookieKey();
        this.REMEMBERME_COOKIE_KEY = options.getRememberMeCookieKey();
        startPeriodicClear();
    }

    /**
     * 启动定时清理，清理过期的session和rememberMe
     */
    @SuppressWarnings("Duplicates")
    private void startPeriodicClear() {

        LocalDateTime now = LocalDateTime.now();

        Long sessionTimeout = this.options.getSessionTimeout();
        this.vertx.setPeriodic(sessionTimeout,id -> {

            Set<Map.Entry<String, Subject>> entrySet = subjectCache.entrySet();
            for(Map.Entry<String, Subject> entry:entrySet){
                if(entry.getValue().time.isBefore(now.minusSeconds(sessionTimeout))){
                    subjectCache.remove(entry.getKey());
                }
            }
        });

        Long rememberMeTimeout = this.options.getRememberMeTimeout();
        this.vertx.setPeriodic(rememberMeTimeout,id -> {
            Set<Map.Entry<String, Subject>> entrySet = rememberMeCache.entrySet();
            for(Map.Entry<String, Subject> entry:entrySet){
                if(entry.getValue().time.isBefore(now.minusSeconds(rememberMeTimeout))){
                    rememberMeCache.remove(entry.getKey());
                }
            }
        });

    }


    public void remove(RoutingContext ctx){
        Cookie jSessionCookie = ctx.getCookie(SESSIONID_COOKIE_KEY);
        if(jSessionCookie != null){
            subjectCache.remove(jSessionCookie.getValue());
        }

        Cookie rememberMeCookie = ctx.getCookie(REMEMBERME_COOKIE_KEY);
        if(rememberMeCookie != null){
            rememberMeCache.remove(rememberMeCookie.getValue());
        }
    }

    public Subject getSubject(RoutingContext ctx) {

        Subject subject = null;

        Cookie rememberMeCookie = ctx.getCookie(REMEMBERME_COOKIE_KEY);
        if(rememberMeCookie != null){
            subject = rememberMeCache.get(rememberMeCookie.getValue());
            if(subject == null){
                subject = getOrCreateSubject(ctx);
            }
        }else{
            subject = getOrCreateSubject(ctx);
        }

        return subject;

    }

    private Subject getOrCreateSubject(RoutingContext ctx){
        //由于在处理器SimpleAuthHandlerImpl进行权限校验前，进行了JSESSIONID的检查，所以此处可以保证jSessionId一定存在
        String jSessionIdFromCookie = ctx.getCookie(SESSIONID_COOKIE_KEY).getValue();
        String jSessionIdFromCtx = ctx.get(SimpleAuthConstants.JESSIONID_KEY);
        String jSessionId = jSessionIdFromCookie != null ? jSessionIdFromCookie : jSessionIdFromCtx;
        Subject subject = subjectCache.get(jSessionId);
        if(subject == null){
            Subject newSubject = new Subject(jSessionId,this.vertx,authProvider,this,this.encryption,this.options);
            return subjectCache.putIfAbsent(jSessionId,newSubject) == null ? newSubject : subjectCache.get(jSessionId);
        }
        return subject;
    }

    public void cacheRememberMe(String key,Subject subject){
        rememberMeCache.put(key, subject);
    }

}
