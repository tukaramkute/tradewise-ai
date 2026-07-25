package com.tradewise.auth.dto.request;

import com.tradewise.common.validation.FieldMatch;
import com.tradewise.common.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Registration payload. Class-level {@link FieldMatch} enforces that the
 * password confirmation matches.
 */
@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword",
        message = "Password and confirm password must match")
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = ValidationPatterns.USERNAME, message = ValidationPatterns.USERNAME_MESSAGE)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    @Size(max = 120)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationPatterns.PHONE, message = ValidationPatterns.PHONE_MESSAGE)
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Time zone is required")
    private String timeZone;

    @NotBlank(message = "Preferred currency is required")
    @Size(min = 3, max = 3, message = "Preferred currency must be a 3-letter ISO code")
    private String preferredCurrency;

    /** Optional at registration. */
    private String profileImageUrl;
}
