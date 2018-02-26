package fun.bookish.vertx.auth.simple.ext;

import io.vertx.core.http.HttpServerRequest;

/**
 * 权限字符串策略接口
 **/
public interface PermissionStrategy {

    /**
     * 根据http请求生成权限字符串
     * @param request http请求
     * @return
     */
    String create(HttpServerRequest request);

    /**
     * 校验请求与缓存的权限字符串
     * @param requestPermission 由http请求生成的请求权限字符串
     * @param cachedPermission    SimpleAuthUser中缓存的权限字符串
     * @return  匹配成功返回true，说明权限认证成功
     *          否则返回false
     */
    boolean match(String requestPermission,String cachedPermission);

}
