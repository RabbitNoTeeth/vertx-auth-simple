package fun.bookish.vertx.auth.simple.manager;

import fun.bookish.vertx.auth.simple.constant.SimpleConfigConstants;
import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryptMode;
import fun.bookish.vertx.auth.simple.encryption.SimpleEncryption;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.user.SimpleAuthUser;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
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

    private static final ConcurrentHashMap<String, Subject> subjectCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Subject> rememberMeCache = new ConcurrentHashMap<>();

    private final Vertx vertx;
    private final SimpleAuthProvider authProvider;
    private final SimpleEncryption encryption;
    private final JsonObject config;

    public SecurityManager(Vertx vertx,SimpleAuthProvider authProvider,SimpleEncryption encryption,JsonObject config){
        this.vertx = vertx;
        this.authProvider = authProvider;
        this.encryption = encryption;
        this.config = config;
        startPeriodicClear();

    }

    /**
     * 启动定时清理，清理过期的session和rememberMe
     */
    @SuppressWarnings("Duplicates")
    private void startPeriodicClear() {

        LocalDateTime now = LocalDateTime.now();

        Long sessionTimeout = this.config.getLong(SimpleConfigConstants.SESSION_TIMEOUT);
        this.vertx.setPeriodic(sessionTimeout,id -> {

            Set<Map.Entry<String, Subject>> entrySet = subjectCache.entrySet();
            for(Map.Entry<String, Subject> entry:entrySet){
                if(entry.getValue().time.isBefore(now.minusSeconds(sessionTimeout))){
                    subjectCache.remove(entry.getKey());
                }
            }
        });

        Long rememberMeTimeout = this.config.getLong(SimpleConfigConstants.REMEMBERME_TIMEOUT);
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
        Cookie jSessionCookie = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY);
        if(jSessionCookie != null){
            subjectCache.remove(jSessionCookie.getValue());
        }

        Cookie rememberMeCookie = ctx.getCookie(SimpleConstants.COOKIE_REMEMBERME_KEY);
        if(rememberMeCookie != null){
            rememberMeCache.remove(rememberMeCookie.getValue());
        }
    }

    public Subject getSubject(RoutingContext ctx) {

        Subject subject = null;

        Cookie rememberMeCookie = ctx.getCookie(SimpleConstants.COOKIE_REMEMBERME_KEY);
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
        //由于在处理器SimpleAuthHandlerImpl进行权限校验前，进行了JSESSIONID cookie的检查，所以此处可以保证jSessionId一定存在
        String jSessionId = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY).getValue();
        Subject subject = subjectCache.get(jSessionId);
        if(subject == null){
            Subject newSubject = new Subject(authProvider,this,this.encryption,this.config);
            if(subjectCache.putIfAbsent(jSessionId,newSubject) == null){
                subject = newSubject;
            }else{
                subject = subjectCache.get(jSessionId);
            }
        }
        return subject;
    }

    public void cacheRememberMe(String key,Subject subject){
        rememberMeCache.put(key, subject);
    }

}
