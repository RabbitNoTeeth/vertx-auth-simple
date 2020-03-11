package fun.bookish.vertx.auth.simple.configurable;

import io.vertx.core.http.HttpServerRequest;

import java.util.Set;

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
     * 检查是否为匿名权限
     * @param requestPermission
     * @return
     */
    boolean checkIfAnonymous(String requestPermission);

    /**
     * 校验权限字符串
     * @param requestPermission 请求的权限字符串
     * @param userPermissions  User中缓存的权限字符串
     * @return
     */
    boolean checkPermission(String requestPermission, Set<String> userPermissions);

}
