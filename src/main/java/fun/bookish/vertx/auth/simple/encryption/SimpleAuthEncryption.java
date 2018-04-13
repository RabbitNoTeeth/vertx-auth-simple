package fun.bookish.vertx.auth.simple.encryption;

public interface SimpleAuthEncryption {

    String encryptOrDecrypt(String data,String key,SimpleAuthEncryptMode mode);

}
