package com.tradewise.auth.dto.request;

import com.tradewise.common.validation.FieldMatch;
import com.tradewise.common.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Resets a password using the OTP delivered during the forgot-password flow.
 */
@Getter
@Setter
@FieldMatch(first = "newPassword", second = "confirmPassword",
        message = "Password and confirm password must match")
public class ResetPasswordRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @NotBlank(message = "OTP is required")
    private String otp;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
