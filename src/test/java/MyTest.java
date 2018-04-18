import io.vertx.core.Vertx;

public class MyTest {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new TestHttpVerticle(),res -> {
            if(res.succeeded()){
                System.out.println("启动成功！");
            }
        });

    }

}
