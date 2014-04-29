package com.carusliu.opendoor.tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具�?
 * */

public class MD5Util
{
    public static final String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            String md5s =  hexString.toString();
            DebugLog.logv("after MD5", md5s);
            return md5s;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        DebugLog.loge("MD5 Error",s);
        return "";
    }
}
