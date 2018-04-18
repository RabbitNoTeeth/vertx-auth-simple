import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.handler.SimpleAuthHandler;
import fun.bookish.vertx.auth.simple.util.SubjectUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.ArrayList;
import java.util.List;

public class TestHttpVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        Router router = Router.router(this.vertx);

        router.route().handler(CookieHandler.create());

        SimpleAuthOptions options = new SimpleAuthOptions();
        List<String> list = new ArrayList<>();
        list.add("GET:/hello");
        list.add("GET:/login");
        list.add("GET:/logout");
        options.setAnnoPermissions(list);

        SimpleAuthHandler simpleAuthHandler = SimpleAuthHandler.create(this.vertx,new AuthProviderImpl(),options);

        router.route().handler(simpleAuthHandler);

        router.route().handler(ctx -> {
            ctx.response().putHeader("Content-Type", "application/json;charset=UTF-8");
            ctx.response().setChunked(true);
            ctx.next();
        });

        router.get("/hello").handler(ctx -> {
            ctx.response().end("hello!");
        });

        router.get("/test").handler(ctx -> {
           ctx.response().end("test passed!");
        });

        router.get("/ttest").handler(ctx -> {
            ctx.response().end("ttest passed!");
        });

        router.get("/login").handler(ctx -> {
            SubjectUtil.getSubject(ctx).enableRememberMe(true).login(ctx,new JsonObject(),res -> {
                if(res.succeeded()){
                    ctx.response().end("登陆成功!");
                }
            });
        });

        router.get("/logout").handler(ctx -> {
            SubjectUtil.getSubject(ctx).logout(ctx);
            ctx.response().end("注销成功!");
        });

        this.vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    }
}
