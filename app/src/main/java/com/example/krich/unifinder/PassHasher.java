package com.example.krich.unifinder;

import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by krich on 07-Oct-17.
 */

class PassHasher {
    public PassHasher(){
    }

    public String Hash(String password){
        String hashedPass = "";

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(password.getBytes(StandardCharsets.US_ASCII));

            byte[] Hashed = sha1.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder buff = new StringBuilder();
            for (byte b : Hashed) {
                String conversion = Integer.toString(b & 0xFF,16);
                while (conversion.length() < 2) {
                    conversion = "0" + conversion;
                }
                buff.append(conversion);
            }
            hashedPass = buff.toString();
        }
        catch (NoSuchAlgorithmException e) {
        }

        return hashedPass;
    }
}
