package com.iblock.common.utils;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by baidu on 16/7/30.
 */
public class MD5Utils {

    private static final String SECRET_KEY = "fasdfadfljladnvfadsfef13vdw137hf";

    private static byte[] desEncryptToBytes(String content, String encryptKey) throws Exception {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(encryptKey.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
            return cipher.doFinal(content.getBytes("UTF-8"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String content) {
        try {
            return base64Encode(desEncryptToBytes(content, SECRET_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    private static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }
}
