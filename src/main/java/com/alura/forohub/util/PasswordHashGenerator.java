package com.alura.forohub.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase utility para generar un hash BCrypt localmente.
 * Run as Java application in your IDE and copy the printed hash.
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        String raw = "admin"; // Cambialo por la contraseña que quieras
        if (args.length > 0) raw = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String hash = encoder.encode(raw);
        System.out.println("Password (plain): " + raw);
        System.out.println("BCrypt hash: " + hash);
    }
}
