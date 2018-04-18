package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.core.http.HttpServerRequest;

/**
 * 权限字符串生成及校验策略接口
 **/
public interface PermissionStrategy {

    /**
     * 根据request请求生成权限字符串
     * @param request
     * @return
     */
    String generatePermission(HttpServerRequest request);

    /**
     * 校验权限字符串
     * @param requestPermission 请求的权限字符串
     * @param cachedPermission  User中缓存的权限字符串
     * @return
     */
    boolean checkPermission(String requestPermission, String cachedPermission);

}
