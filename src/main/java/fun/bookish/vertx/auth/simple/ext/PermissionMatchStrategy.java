package fun.bookish.vertx.auth.simple.ext;


/**
 * 权限字符串生成策略接口
 **/
public interface PermissionMatchStrategy {

    /**
     * 校验请求与缓存的权限字符串
     * @param request 由http请求生成的请求权限字符串
     * @param cached    SimpleAuthUser中缓存的权限字符串
     * @return  匹配成功返回true，说明权限认证成功
     *          否则返回false
     */
    boolean match(String request,String cached);

}
