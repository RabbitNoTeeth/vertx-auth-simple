package fun.bookish.vertx.auth.simple.configurable.impl;

import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import io.vertx.core.http.HttpServerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 字符串生成策略接口默认实现
 **/
public class DefaultPermissionStrategyImpl implements PermissionStrategy {

    private List<String> defaultAnonymous = Arrays.asList(".html", ".css", ".json", ".text", ".js", ".woff", ".svg", ".ttf", ".ico", ".png");

    @Override
    public String generatePermission(HttpServerRequest request){
        return request.method().name()+":" + request.path();
    }

    @Override
    public boolean checkIfAnonymous(String requestPermission) {
        return defaultAnonymous.stream().anyMatch(requestPermission::endsWith);
    }

    @Override
    public boolean checkPermission(String requestPermission, Set<String> userPermissions){
        return userPermissions.stream().anyMatch(up -> up.equals(requestPermission));
    }

}
