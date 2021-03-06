package com.essheva.wordMemo.crypto;

import com.essheva.wordMemo.exceptions.InternalServerError;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

@Slf4j
public class EncryptionUtils {

    private static final Random GENERATOR = new SecureRandom();
    private static final String ALNUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int TOKEN_CAPACITY = 32;
    private static final int DEFUALT_SALT_LENGTH = 64;
    private static final int KEY_LENGTH = 256;

    public static String generateSalt(int length) {
        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            salt.append(ALNUM.charAt(GENERATOR.nextInt(ALNUM.length())));
        }
        return salt.toString();
    }

    public static String generateSalt() {
        return generateSalt(DEFUALT_SALT_LENGTH);
    }

    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error during hashing a password.", e);
            throw new InternalServerError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public static String generateSecurePassword(String password, String salt) {
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
        return Base64.getEncoder().encodeToString(securePassword);
    }

    public static boolean matchUserPassword(String password, String securedPassword, String salt) {
        return generateSecurePassword(password, salt).equals(securedPassword);
    }

    public static String generateId() {
        byte[] randomBytes = new byte[32];
        GENERATOR.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public static String generateToken() {
        StringBuilder token = new StringBuilder(TOKEN_CAPACITY);
        for (int i = 0; i < TOKEN_CAPACITY; i++) {
            token.append(ALNUM.charAt(GENERATOR.nextInt(ALNUM.length())));
        }
        return token.toString();
    }
}
