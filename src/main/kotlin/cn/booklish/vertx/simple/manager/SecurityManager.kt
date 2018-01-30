package cn.booklish.vertx.simple.manager

import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.web.RoutingContext
import cn.booklish.vertx.simple.core.Subject
import java.util.concurrent.ConcurrentHashMap


/**
 * 安全管理器,存储subject用户
 */
object SecurityManager {

    private val subjectMap = ConcurrentHashMap<String, Subject>()
    private val rememberMeSubjectMap = ConcurrentHashMap<String, Subject>()

    private lateinit var authProvider:AuthProvider

    fun initAuthProvider(authProvider:AuthProvider){
        SecurityManager.authProvider = authProvider
    }

    fun cacheSubject(key:String,value: Subject){
        subjectMap.put(key, value)
    }

    fun cacheRememberMeSubject(key:String,value: Subject){
        rememberMeSubjectMap.put(key, value)
    }

    fun getSubject(ctx:RoutingContext): Subject {

        val rememberMeCookie = ctx.getCookie("RememberMe")
        if(rememberMeCookie == null){
            val jSessionId = ctx.getCookie("JSESSIONID").value
            var subject = subjectMap[jSessionId]
            if(subject == null){
                val newSubject = Subject(authProvider)
                if(subjectMap.putIfAbsent(jSessionId,newSubject) == null){
                    subject = newSubject
                }
            }
            return subject!!
        }else{
            val rememberMe = rememberMeCookie.value
            var subject = rememberMeSubjectMap[rememberMe]
            if(subject == null){
                val newSubject = Subject(authProvider)
                if(rememberMeSubjectMap.putIfAbsent(rememberMe,newSubject) == null){
                    subject = newSubject
                }
            }
            return subject!!
        }

    }

}