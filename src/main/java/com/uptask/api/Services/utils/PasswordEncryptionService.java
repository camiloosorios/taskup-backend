package com.uptask.api.Services.utils;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class PasswordEncryptionService {

    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public String encryptPassword(String password) {
        try {
            byte[] salt = generateSalt();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            byte[] saltedHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hash, 0, saltedHash, salt.length, hash.length);
            return Base64.getEncoder().encodeToString(saltedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyPassword(String passwordEntered, String passwordStored) {
        try {
            byte[] saltedHash = Base64.getDecoder().decode(passwordStored);
            byte[] salt = new byte[16];
            System.arraycopy(saltedHash, 0, salt, 0, salt.length);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(passwordEntered.getBytes());
            byte[] comparacionHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, comparacionHash, 0, salt.length);
            System.arraycopy(hash, 0, comparacionHash, salt.length, hash.length);
            return MessageDigest.isEqual(saltedHash, comparacionHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

}