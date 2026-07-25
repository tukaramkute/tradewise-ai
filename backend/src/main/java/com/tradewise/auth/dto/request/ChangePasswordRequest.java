package com.tradewise.auth.dto.request;

import com.tradewise.common.validation.FieldMatch;
import com.tradewise.common.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Changes the password for an authenticated user after verifying the old one.
 */
@Getter
@Setter
@FieldMatch(first = "newPassword", second = "confirmPassword",
        message = "Password and confirm password must match")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationPatterns.PASSWORD_MESSAGE)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
