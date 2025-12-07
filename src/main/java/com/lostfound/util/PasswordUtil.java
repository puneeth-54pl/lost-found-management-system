package com.lostfound.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Hash a password with a randomly generated salt
     * @param password Plain text password
     * @return Base64 encoded string containing salt + hash
     */
    public static String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Create hash
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            // Combine salt and hash
            byte[] saltAndHash = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);

            // Return as Base64 string
            return Base64.getEncoder().encodeToString(saltAndHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verify a password against a stored hash
     * @param password Plain text password to verify
     * @param storedHash Base64 encoded string containing salt + hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Decode the stored hash
            byte[] saltAndHash = Base64.getDecoder().decode(storedHash);
            if (saltAndHash.length < SALT_LENGTH) {
                return false; // Invalid hash format
            }

            // Extract salt and hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] storedHashBytes = new byte[saltAndHash.length - SALT_LENGTH];
            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(saltAndHash, SALT_LENGTH, storedHashBytes, 0, storedHashBytes.length);

            // Hash the input password with the extracted salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] inputHash = md.digest(password.getBytes());

            // Compare hashes
            return MessageDigest.isEqual(inputHash, storedHashBytes);

        } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
            return false; // Invalid hash or algorithm not available
        }
    }
}
