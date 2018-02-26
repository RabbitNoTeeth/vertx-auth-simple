package fun.bookish.vertx.auth.simple.ext;

import io.vertx.core.http.HttpServerRequest;

/**
 * 权限字符串生成策略接口默认实现
 **/
public class DefaultPermissionCreateStrategyImpl implements PermissionCreateStrategy{

    public String create(HttpServerRequest request){
        return request.method().name()+":" + request.path();
    }

}
