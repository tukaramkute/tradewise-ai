package com.tradewise.common.validation;

/**
 * Shared validation constants and regular expressions.
 */
public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    /**
     * Minimum 8 chars, at least one uppercase, one lowercase, one digit and one
     * special character.
     */
    public static final String PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";

    public static final String PASSWORD_MESSAGE =
            "Password must be at least 8 characters and include an uppercase letter, "
                    + "a lowercase letter, a number and a special character";

    /** E.164-ish phone number: optional leading +, 7–15 digits. */
    public static final String PHONE = "^\\+?[1-9]\\d{6,14}$";

    public static final String PHONE_MESSAGE = "Phone number must be a valid international number (7-15 digits)";

    /** Username: 3–30 chars, letters, digits, dot, underscore, hyphen. */
    public static final String USERNAME = "^[A-Za-z0-9._-]{3,30}$";

    public static final String USERNAME_MESSAGE =
            "Username must be 3-30 characters using letters, digits, '.', '_' or '-'";
}
