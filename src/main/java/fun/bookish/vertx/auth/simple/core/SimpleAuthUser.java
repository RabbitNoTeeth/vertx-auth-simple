package fun.bookish.vertx.auth.simple.core;

import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultPermissionStrategyImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

import java.util.Collection;


public class SimpleAuthUser extends AbstractUser {

    private final PermissionStrategy permissionStrategy;

    public SimpleAuthUser() {
        this.permissionStrategy = new DefaultPermissionStrategyImpl();
    }

    public SimpleAuthUser(PermissionStrategy permissionStrategy) {
        this.permissionStrategy = permissionStrategy;
    }

    private volatile JsonObject principal;

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        boolean access = this.permissionStrategy.checkPermission(permission, this.cachedPermissions);
        resultHandler.handle(Future.succeededFuture(access));
    }

    @Override
    public JsonObject principal() {
        return this.principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
    }

    public void setPrincipal(JsonObject principal) {
        this.principal = principal;
    }

    public void addPermission(String permission) {
        this.cachedPermissions.add(permission);
    }

    public void addPermissions(Collection<String> permissions) {
        this.cachedPermissions.addAll(permissions);
    }
}
