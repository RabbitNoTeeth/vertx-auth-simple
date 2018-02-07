package fun.bookish.vertx.auth.simple.handler;

import fun.bookish.vertx.auth.simple.constant.SimpleConstants;
import fun.bookish.vertx.auth.simple.core.Subject;
import fun.bookish.vertx.auth.simple.manager.SecurityManager;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import fun.bookish.vertx.auth.simple.util.SimpleUtils;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Don9
 * @create 2018-02-07-14:58
 **/
public class SimpleAuthHandlerImpl implements SimpleAuthHandler{


    private final SimpleAuthProvider simpleAuthProvider;

    public SimpleAuthHandlerImpl(SimpleAuthProvider simpleAuthProvider){
        this.simpleAuthProvider = simpleAuthProvider;
        SecurityManager.initAuthProvider(simpleAuthProvider);
    }

    private final List<String> annoPermissionList = new ArrayList<>();

    /**
     * 验证请求资源是否支持匿名访问
     */
    @Override
    public boolean checkAnno(String permission) {
        return this.annoPermissionList.contains("*") || this.annoPermissionList.contains(permission) ||
                this.annoPermissionList.stream().anyMatch(per -> permission.startsWith(per.replaceAll("\\*", "")));
    }

    /**
     * 检查JSESSIONID cookie,如果请求中没有该cookie,则创建JSESSIONID cookie写入到响应中
     */
    @Override
    public void checkCookie(RoutingContext ctx) {
        if(ctx.getCookie(SimpleConstants.COOKIE_JSESSIONID_KEY) == null){
            ctx.addCookie(Cookie.cookie(SimpleConstants.COOKIE_JSESSIONID_KEY, SimpleUtils.getUUID()).setHttpOnly(true).setPath("/"));
        }
    }

    /**
     * 添加匿名访问许可
     */
    @Override
    public SimpleAuthHandler addAnnoPermissions(List<String> permissions) {
        this.annoPermissionList.addAll(permissions);
        return this;
    }

    /**
     * 添加匿名访问许可
     */
    @Override
    public SimpleAuthHandler addAnnoPermission(String permission) {
        this.annoPermissionList.add(permission);
        return this;
    }

    @Override
    public void handle(RoutingContext ctx) {
        //先检查 JSESSIONID cookie
        checkCookie(ctx);

        HttpMethod method = ctx.request().method();

        //拼接权限字符串
        String permission = method.name()+":"+ctx.request().path();

        //获取当前用户
        Subject subject = SecurityManager.getSubject(ctx);

        if(method == HttpMethod.OPTIONS || checkAnno(permission)){
            //将当前用户信息放入router上下文中,便于子路由获取.(此时用户可能未登录,也可能已登录)
            ctx.put(SimpleConstants.CTX_SUBJECT_KEY,subject);
            ctx.put(SimpleConstants.CTX_START_TIME_KEY,System.nanoTime());
            ctx.next();
        }else{
            if(subject.isAuthenticated()){
                subject.isAuthorised(permission, res -> {
                    if(res.succeeded() && res.result()){
                        //将当前用户信息放入router上下文中,便于子路由获取.(此时用户已登录)
                        ctx.put(SimpleConstants.CTX_SUBJECT_KEY,subject);
                        ctx.put(SimpleConstants.CTX_START_TIME_KEY,System.nanoTime());
                        ctx.next();
                    }else{
                        ctx.response().setStatusCode(403).end("you have no permission");
                    }
                });
            }else{
                ctx.response().setStatusCode(403).end("you need login first");
            }
        }
    }
}
