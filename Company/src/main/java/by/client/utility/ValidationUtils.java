package by.client.utility;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 15;
    private static final int MIN_LOGIN_LENGTH = 4;
    private static final int MAX_LOGIN_LENGTH = 14;
    private static final int MIN_PRODUCT_NAME_LENGTH = 3;
    private static final int MAX_PRODUCT_NAME_LENGTH = 30;
    private static final BigDecimal MAX_BIG_DECIMAL_VALUE = new BigDecimal("20000");

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zА-Яа-яЁё]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    public static boolean validateName(String name) {
        if (isNullOrEmpty(name)) return false;
        return validateStringWithPattern(name, NAME_PATTERN, MIN_NAME_LENGTH, MAX_NAME_LENGTH);
    }

    public static boolean validateSurname(String surname) {
        if (isNullOrEmpty(surname)) return false;
        return validateName(surname);
    }

    public static boolean validateLogin(String login) {
        if (isNullOrEmpty(login)) return false;
        return login.length() >= MIN_LOGIN_LENGTH && login.length() <= MAX_LOGIN_LENGTH;
    }

    public static boolean validateEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validatePassword(String password) {
        if (isNullOrEmpty(password)) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validateProductName(String productName) {
        if (isNullOrEmpty(productName)) return false;
        return productName.length() >= MIN_PRODUCT_NAME_LENGTH && productName.length() <= MAX_PRODUCT_NAME_LENGTH;
    }

    public static boolean validateBigDecimalField(String value) {
        try {
            if (isNullOrEmpty(value)) return false;
            BigDecimal decimalValue = new BigDecimal(value);
            return decimalValue.compareTo(BigDecimal.ZERO) >= 0 && decimalValue.compareTo(MAX_BIG_DECIMAL_VALUE) <= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateStringWithPattern(String input, Pattern pattern, int minLength, int maxLength) {
        if (isNullOrEmpty(input)) return false;
        return input.length() >= minLength && input.length() <= maxLength && pattern.matcher(input).matches();
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}