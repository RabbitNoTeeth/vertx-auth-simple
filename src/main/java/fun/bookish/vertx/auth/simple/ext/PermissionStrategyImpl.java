package fun.bookish.vertx.auth.simple.ext;

import io.vertx.core.http.HttpServerRequest;

/**
 * 权限字符串策略接口默认实现
 **/
public class PermissionStrategyImpl implements PermissionStrategy {

    @Override
    public String create(HttpServerRequest request){
        return request.method().name()+":" + request.path();
    }

    @Override
    public boolean match(String request, String cached){
        return  cached.equals("*") || request.startsWith(cached.replaceAll("\\*", ""));
    }

}
