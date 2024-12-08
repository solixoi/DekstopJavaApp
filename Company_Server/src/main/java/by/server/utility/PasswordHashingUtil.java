package by.server.utility;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.nio.charset.StandardCharsets;

public class PasswordHashingUtil {

    public static String hashPassword(String plainPassword) {
        Argon2 argon2 = Argon2Factory.create();
        try {
            return argon2.hash(10, 65536, 1, plainPassword.getBytes(StandardCharsets.UTF_8));
        } finally {
            argon2.wipeArray(plainPassword.toCharArray());
        }
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.verify(hashedPassword, plainPassword.getBytes(StandardCharsets.UTF_8));
    }
}
