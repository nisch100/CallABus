package com.example.nisch100.call_a_bus;

import android.util.Log;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HashPassword {

    static String hashing(String password){

        // Fields for hashing password
        SecretKeyFactory factory;
        final String hashAlgorithm = "PBKDF2WithHmacSHA1";
        SecureRandom random;
        byte[] salt;
        KeySpec spec;
        byte[] hash;

        // Procedures to hash password
        try
        {
            // prepare salt

            /*
            random = new SecureRandom();
            salt = new byte[16];
            random.nextBytes(salt);
            */

            salt = "abcdabcdabcdabcd".getBytes();

            // prepare PBEKeySpec
            spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

            // prepare hash algorithm
            factory = SecretKeyFactory.getInstance(hashAlgorithm);

            // generate hash
            hash = factory.generateSecret(spec).getEncoded();

            return new String(hash);
        }
        catch (NoSuchAlgorithmException e1){
            Log.e("algorithm", "Cannot find hash algorithm: " + hashAlgorithm);
            return null;
        }
        catch (InvalidKeySpecException e2){
            Log.e("spec", "Invalid Key Spec");
            return null;
        }

    }

}
