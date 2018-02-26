package fun.bookish.vertx.auth.simple.ext;


/**
 * 权限字符串生成策略接口
 **/
public class DefaultPermissionMatchStrategyImpl implements PermissionMatchStrategy{

    public boolean match(String request, String cached){
       return  cached.equals("*") || request.startsWith(cached.replaceAll("\\*", ""));
    }

}
