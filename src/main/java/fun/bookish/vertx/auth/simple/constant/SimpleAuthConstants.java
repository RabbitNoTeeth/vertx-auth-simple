package fun.bookish.vertx.auth.simple.constant;


import fun.bookish.vertx.auth.simple.configurable.PermissionStrategy;
import fun.bookish.vertx.auth.simple.configurable.RealmStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionIdStrategy;
import fun.bookish.vertx.auth.simple.configurable.SessionPersistStrategy;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultPermissionStrategyImpl;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultRealmStrategyImpl;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultSessionIdStrategyImpl;
import fun.bookish.vertx.auth.simple.configurable.impl.DefaultSessionPersistStrategyImpl;


public class SimpleAuthConstants {

    private SimpleAuthConstants(){}

    public static final String SUBJECT_KEY_IN_SESSION = "SIMPLE-AUTH-SUBJECT";

    public static final PermissionStrategy DEFAULT_PERMISSION_STRATEGY_IMPL = new DefaultPermissionStrategyImpl();

    public static final RealmStrategy DEFAULT_REALM_STRATEGY_IMPL = new DefaultRealmStrategyImpl();

    public static final SessionIdStrategy DEFAULT_SESSION_ID_STRATEGY_IMPL = new DefaultSessionIdStrategyImpl();

    public static final SessionPersistStrategy DEFAULT_SESSION_PERSIST_STRATEGY_IMPL = new DefaultSessionPersistStrategyImpl();

}
