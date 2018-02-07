package fun.bookish.vertx.auth.simple.util;

import java.util.UUID;

/**
 * 工具类
 */
public class SimpleUtils {

    private SimpleUtils(){}

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
