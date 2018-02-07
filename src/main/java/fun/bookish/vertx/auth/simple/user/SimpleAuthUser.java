package fun.bookish.vertx.auth.simple.user;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * io.vertx.ext.auth.User接口实现
 */
public class SimpleAuthUser extends AbstractUser {

    private static final AtomicReference<AuthProvider> authProviderRef = new AtomicReference<>();

    private static final AtomicReference<JsonObject> principalRef = new AtomicReference<>();

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        boolean access = this.cachedPermissions.contains("*") ||
                this.cachedPermissions.stream().anyMatch(per -> per.startsWith(permission.replace("*", "")));
        resultHandler.handle(Future.succeededFuture(access));
    }

    @Override
    public JsonObject principal() {
        return principalRef.get();
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        authProviderRef.compareAndSet(null,authProvider);
    }

    public void setPrincipal(JsonObject principal){
        principalRef.compareAndSet(null,principal);
    }

    public void  appendPermission(String permission){
        this.cachedPermissions.add(permission);
    }

    public void  appendPermissions(Collection<String> permissions){
        this.cachedPermissions.addAll(permissions);
    }
}
