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

    private static final ConcurrentHashMap<String, Subject> subjectMap = new ConcurrentHashMap<>();

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
     * 启动定时清理，清理过期的session
     */
    private void startPeriodicClear() {
        Long sessionTimeout = this.config.getLong(SimpleConfigConstants.SESSION_TIMEOUT);
        this.vertx.setPeriodic(sessionTimeout,id -> {
            LocalDateTime now = LocalDateTime.now();
            Set<Map.Entry<String, Subject>> entrySet = subjectMap.entrySet();
            for(Map.Entry<String, Subject> entry:entrySet){
                if(entry.getValue().time.isBefore(now.minusSeconds(sessionTimeout))){
                    subjectMap.remove(entry.getKey());
                }
            }
        });
    }


    public void remove(RoutingContext ctx){
        Cookie jSessionCookie = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY);
        if(jSessionCookie != null){
            subjectMap.remove(jSessionCookie.getValue());
        }
    }

    public Subject getSubject(RoutingContext ctx) {

        //由于在处理器SimpleAuthHandlerImpl进行权限校验前，进行了JSESSIONID cookie的检查，所以此处可以保证jSessionId一定存在
        String jSessionId = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY).getValue();
        Subject subject = subjectMap.get(jSessionId);
        if(subject == null){
            Subject newSubject = new Subject(authProvider,this.encryption,this.config);
            if(subjectMap.putIfAbsent(jSessionId,newSubject) == null){
                subject = newSubject;
            }
        }

        Cookie rememberMe = ctx.getCookie(SimpleConstants.COOKIE_REMEMBERME_KEY);
        if(rememberMe !=null && !subject.isAuthenticated()){
            subject.setUser(new SimpleAuthUser(new JsonObject(this.encryption.encryptOrDecrypt(rememberMe.getValue()
                    ,this.config.getString(SimpleConfigConstants.AES_KEY), SimpleEncryptMode.DECRYPT))));
            subject.setRememberMe();
        }
        return subject;

    }

}
