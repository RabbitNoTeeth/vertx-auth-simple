package fun.bookish.vertx.auth.simple.manager;

import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 安全管理器,用户获取当前subject实体
 */
public class SecurityManager {

    private SecurityManager(){}

    private static final ConcurrentHashMap<String, Subject> subjectMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, Subject> rememberMeSubjectMap = new ConcurrentHashMap<>();

    private static AuthProvider authProvider;

    private static final Object lock = new Object();

    public static void initAuthProvider(AuthProvider provider){
        if(authProvider == null){
            synchronized (lock){
                if(authProvider == null){
                    authProvider = provider;
                }
            }
        }
    }

    public static void cacheSubject(String key,Subject value){
        subjectMap.put(key, value);
    }

    public static void cacheRememberMeSubject(String key,Subject value){
        rememberMeSubjectMap.put(key, value);
    }

    public static Subject getSubject(RoutingContext ctx) {

        Cookie rememberMeCookie = ctx.getCookie(SimpleConstants.COOKIE_REMEMBERME_KEY);
        if(rememberMeCookie == null){
            String jSessionId = ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY).getValue();
            Subject subject = subjectMap.get(jSessionId);
            if(subject == null){
                Subject newSubject = new Subject(jSessionId ,authProvider);
                if(subjectMap.putIfAbsent(jSessionId,newSubject) == null){
                    subject = newSubject;
                }
            }
            return subject;
        }else{
            String rememberMe = rememberMeCookie.getValue();
            Subject subject = rememberMeSubjectMap.get(rememberMe);
            if(subject == null){
                Subject newSubject = new Subject(rememberMe, authProvider);
                if(rememberMeSubjectMap.putIfAbsent(rememberMe,newSubject) == null){
                    subject = newSubject;
                }
            }
            return subject;
        }

    }

}
