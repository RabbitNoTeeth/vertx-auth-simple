package fun.bookish.vertx.auth.simple.util;

import fun.bookish.vertx.auth.simple.constant.SimpleAuthConstants;
import fun.bookish.vertx.auth.simple.core.SimpleAuthOptions;
import fun.bookish.vertx.auth.simple.core.Subject;
import io.vertx.ext.web.RoutingContext;

public class SubjectUtil {

    private SubjectUtil(){}

    public static Subject getSubject(RoutingContext context){
        return context.session().get(SimpleAuthConstants.SUBJECT_KEY_IN_SESSION);
    }

}
