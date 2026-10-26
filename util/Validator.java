package util;

import java.util.regex.Pattern;

public class Validator {
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String MOBILE_REGEX = "^[6-9][0-9]{9}$";
    private static final Pattern MOBILE_PATTERN = Pattern.compile(MOBILE_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        return MOBILE_PATTERN.matcher(mobile).matches();
    }
}