package fun.bookish.vertx.auth.simple.user;

import fun.bookish.vertx.auth.simple.ext.PermissionStrategy;
import fun.bookish.vertx.auth.simple.ext.PermissionStrategyImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * io.vertx.ext.auth.User接口实现
 */
public class SimpleAuthUser extends AbstractUser {

    private final PermissionStrategy permissionStrategy;

    public SimpleAuthUser(){
        this.permissionStrategy = new PermissionStrategyImpl();
    }

    public SimpleAuthUser(PermissionStrategy permissionStrategy){
        this.permissionStrategy = permissionStrategy;
    }

    private volatile JsonObject principal;

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        boolean access = this.cachedPermissions.stream().anyMatch(per -> this.permissionStrategy.match(permission,per));
        resultHandler.handle(Future.succeededFuture(access));
    }

    @Override
    public JsonObject principal() {
        return this.principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) { }

    public void setPrincipal(JsonObject principal){
        this.principal = principal;
    }

    public void  appendPermission(String permission){
        this.cachedPermissions.add(permission);
    }

    public void  appendPermissions(Collection<String> permissions){
        this.cachedPermissions.addAll(permissions);
    }
}
