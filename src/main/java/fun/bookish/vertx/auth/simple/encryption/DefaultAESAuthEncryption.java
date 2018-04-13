package fun.bookish.vertx.auth.simple.encryption;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DefaultAESAuthEncryption implements SimpleAuthEncryption {

    private static MessageDigest md5Digest;

    static {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //
        }
    }

    @Override
    public String encryptOrDecrypt(String data, String key, SimpleAuthEncryptMode mode) {
        try {
            if (StringUtils.isBlank(data)) {
                throw new IllegalArgumentException("can not encrypt or decrypt a blank string");
            }
            if (StringUtils.isBlank(key)) {
                throw new IllegalArgumentException("can not encrypt or decrypt without a key");
            }

            boolean encrypt = (mode == SimpleAuthEncryptMode.ENCRYPT);
            int mod = encrypt?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE;
            byte[] content;

            //true 加密内容 false 解密内容
            if (encrypt) {
                content = data.getBytes("utf-8");
            } else {
                content = parseHexStr2Byte(data);
            }

            //构造一个密钥
            SecretKeySpec keySpec = new SecretKeySpec(md5Digest.digest(key.getBytes("utf-8")), "AES");
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化
            cipher.init(mod, keySpec);
            //加密或解密
            byte[] result = cipher.doFinal(content);
            if (encrypt) {
                return parseByte2HexStr(result);
            } else {
                return new String(result, "utf-8");
            }
        } catch (Exception e) {
            throw new RuntimeException("can not encrypt or decrypt",e);
        }

    }

    /**
     * 将二进制转换成16进制
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
