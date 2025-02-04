package com.app_rutas.controller.dao.services.cuentaUtils;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CuentaUtils {
    public static String generarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Error al encriptar la contraseña: " + e.getMessage());
        }
    }

    public static boolean validatePassword(String password, String storedPasswordHash, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String encodedPassword = Base64.getEncoder().encodeToString(hashedPassword);
            return encodedPassword.equals(storedPasswordHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}
