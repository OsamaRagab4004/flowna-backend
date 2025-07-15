package com.flowna.app.auth;

import com.flowna.app.user.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  private static final int PASSWORD_MIN_LENGTH = 8;
  private static final int NAME_MIN_LENGTH = 4;

  @NotBlank(message = "First name is required")
  @Size(min = NAME_MIN_LENGTH, message = "First name must be at least 5 characters long")
  @Column(nullable = false)
  private String firstname;


  @Column(nullable = true)
  private String lastname;


  @Email(message = "Invalid email address")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = PASSWORD_MIN_LENGTH, message = "Password must be at least 8 characters long")
  @Column(nullable = false)
  private String password;


}
