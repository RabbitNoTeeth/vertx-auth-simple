import fun.bookish.vertx.auth.simple.core.SimpleAuthUser;
import fun.bookish.vertx.auth.simple.provider.SimpleAuthProvider;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

public class AuthProviderImpl implements SimpleAuthProvider {
    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        SimpleAuthUser user = new SimpleAuthUser();
        user.addPermission("GET:/test");
        resultHandler.handle(Future.succeededFuture(user));
    }
}
