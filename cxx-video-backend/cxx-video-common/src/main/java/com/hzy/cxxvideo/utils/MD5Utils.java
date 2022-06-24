package com.hzy.cxxvideo.utils;

import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newstr = Base64.encodeBase64String(strValue.getBytes());
        return newstr;
    }

    public static String md5(String strValue) {
        if (StringUtils.isBlank(strValue)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(strValue.getBytes());
    }

    public static void main(String[] args) {
        try {
            String hzy = MD5Utils.getMD5Str("HZY");
            System.out.println(hzy);
            String hzy1 = MD5Utils.md5("HZY");
            System.out.println(hzy1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
