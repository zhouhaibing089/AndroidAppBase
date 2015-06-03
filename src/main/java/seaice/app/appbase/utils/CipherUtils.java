package seaice.app.appbase.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {

    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET = "UTF-8";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


    private static SecretKeySpec generateKey(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = password.getBytes(CHARSET);
            digest.update(bytes, 0, bytes.length);
            byte[] key = digest.digest();
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            return null;
        }
    }

    public static String encrypt(String message, String password) {
        try {
            SecretKeySpec key = generateKey(password);
            byte[] cipherText = encrypt(key, ivBytes, message.getBytes(CHARSET));
            return toHex(cipherText);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] encrypt(final SecretKeySpec key, final byte[] iv, final byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            return cipher.doFinal(message);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String hexEncrypted, String password) {
        try {
            SecretKeySpec key = generateKey(password);
            byte[] decryptedBytes = decrypt(key, ivBytes, toByte(hexEncrypted));
            if (decryptedBytes == null) {
                return null;
            }
            return new String(decryptedBytes, CHARSET);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] decrypt(SecretKeySpec key, byte[] iv, byte[] decodedCipherText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            return cipher.doFinal(decodedCipherText);
        } catch (Exception e) {
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }
}
