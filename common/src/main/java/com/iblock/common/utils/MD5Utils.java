package com.iblock.common.utils;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.security.MessageDigest;

/**
 * Created by baidu on 16/7/30.
 */
public class MD5Utils {

    public static String encrypt(String s) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(s.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            System.out.println(s + " " + Base64.encode(md.digest()));
            return new String(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void main(String[] args) {
        MD5Utils.encrypt("Test1234!");
        MD5Utils.encrypt("Test1234");
        MD5Utils.encrypt("jgjg1jap");

        MD5Utils.encrypt("admin");
    }
}
