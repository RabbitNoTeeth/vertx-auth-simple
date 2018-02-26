package fun.bookish.vertx.auth.simple.ext;

import io.vertx.core.http.HttpServerRequest;

/**
 * 权限字符串生成策略接口
 **/
public interface PermissionCreateStrategy {

    /**
     * 根据http请求生成权限字符串
     * @param request http请求
     * @return
     */
    String create(HttpServerRequest request);

}
