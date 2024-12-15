package by.server.utility;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class PasswordHashingUtil {
    private static final Pattern ARGON2_HASH_PATTERN = Pattern.compile(
            "^\\$argon2(id|i)\\$v=\\d+\\$m=\\d+,t=\\d+,p=\\d+\\$[A-Za-z0-9+/=]+\\$[A-Za-z0-9+/=]+$"
    );

    public static String hashPassword(String plainPassword) {
        if (isArgon2Hash(plainPassword)) {
            return plainPassword;
        }
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

    private static boolean isArgon2Hash(String password) {
        return ARGON2_HASH_PATTERN.matcher(password).matches();
    }
}
