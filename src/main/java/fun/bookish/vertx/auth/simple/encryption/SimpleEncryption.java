package fun.bookish.vertx.auth.simple.encryption;

public interface SimpleEncryption {

    String encryptOrDecrypt(String data,String key,SimpleEncryptMode mode);

}
