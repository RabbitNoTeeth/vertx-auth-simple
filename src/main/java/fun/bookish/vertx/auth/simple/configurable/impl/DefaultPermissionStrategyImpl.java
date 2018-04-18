package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import io.vertx.core.http.HttpServerRequest;

/**
 * 字符串生成策略接口默认实现
 **/
public class DefaultPermissionStrategyImpl implements PermissionStrategy {

    @Override
    public String generatePermission(HttpServerRequest request){
        return request.method().name()+":" + request.path();
    }

    @Override
    public boolean checkPermission(String request, String cached){
        return  cached.equals("*") || request.startsWith(cached.replaceAll("\\*", ""));
    }

}