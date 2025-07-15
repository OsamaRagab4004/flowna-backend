package com.flowna.app.auth;

import com.flowna.app.config.JwtService;
import com.flowna.app.email.EmailService;
import com.flowna.app.token.Token;
import com.flowna.app.token.TokenRepository;
import com.flowna.app.token.TokenType;
import com.flowna.app.user.Role;
import com.flowna.app.user.User;
import com.flowna.app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  @Autowired
  private final TwoFACodeService twoFACodeService;
  @Autowired
  private final EmailService emailService;

    @Value("${application.security.google.id}")
    private String googleClientId;

  public ResponseEntity<?> register(RegisterRequest request) {
    String TwoFAcode = twoFACodeService.generate2FACode();
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.FREE_TRIAL_USER)
        .message2FA(TwoFAcode)
            /**
             * need to be false in production
             * **/
        .verified(false)
        .build();

    var savedUser = repository.save(user);
    Map<String, Object> variables = new HashMap<>();
    variables.put("name", request.getFirstname() + " " + request.getLastname());
    variables.put("token", TwoFAcode);
    variables.put("confirmUrl", "https://app.flowna.io/verify-user?code=" + java.net.URLEncoder.encode(TwoFAcode, java.nio.charset.StandardCharsets.UTF_8) + "&email=" + request.getEmail());
    emailService.sendSimpleEmail(request.getEmail(),
                "Confirmation Code",
                "emailTemplate.html",variables
                );
return ResponseEntity.ok("Please Confirm your email !");
}

 public ResponseEntity<?> authenticate(AuthenticationRequest request, HttpServletResponse response) {
    // Authenticate the user
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );

    // Retrieve the user from the repository
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();

    // Check if the user is verified
    if (!user.isVerified()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Verification failed! Please verify your email.");
    }

    // Generate JWT and refresh token
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    // Revoke old tokens and save the new one
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);

     Map<String, Object> variables = new HashMap<>();
     variables.put("msg", request.getEmail());
     emailService.sendSimpleEmail("webflowna@gmail.com",
             "User Login Notification",
             "messageTemplate.html",variables
     );

   Cookie jwtCookie = new Cookie("refreshToken", refreshToken);
   jwtCookie.setHttpOnly(true);
   jwtCookie.setSecure(false); // false for localhost
   jwtCookie.setPath("/");
   jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
   jwtCookie.setAttribute("SameSite", "None"); // Explicitly set SameSite=None
   response.addCookie(jwtCookie);


    // Return the response with the refresh token
    var authResponse = AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .username(user.getUniqueName())
        .build();
    return ResponseEntity.ok(authResponse);
}




  public boolean VerifyUserCode(AuthenticationResponseTwoFA auth) {
    System.out.println(auth);
    var user = repository.findByEmail(auth.email)
            .orElseThrow();
    String DB_Code = user.getMessage2FA();
    System.out.println("DB : " + DB_Code);
    System.out.println("Param: " + auth.getTwoFactor());
    if(DB_Code.equals(auth.getTwoFactor())) {
        user.setVerified(true);
        user.setMessage2FA(null);
        repository.save(user);
        return true;
    }
    return false;
  }



  public ResponseEntity<?> googleAuthenticateUser(Map<String, String> payload, HttpServletResponse response) {
    String idTokenString = payload.get("token");
      GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
              .setAudience(Collections.singletonList(googleClientId))
              .build();

    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        GoogleIdToken.Payload idTokenPayload = idToken.getPayload();
        String userId = idTokenPayload.getSubject();
        String email = idTokenPayload.getEmail();
        String name = (String) idTokenPayload.get("name");


        Map<String, Object> variables = new HashMap<>();
          variables.put("msg",name);
          emailService.sendSimpleEmail("webflowna@gmail.com",
                  "User Login Notification",
                  "messageTemplate.html",variables
          );

        Optional<User> user = repository.findByEmail(email);
        if(user.isPresent()) {
          // if user is existed and authenticate with google
          var authenticated_existed_user = user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
          var jwtToken = jwtService.generateToken(authenticated_existed_user);
          var refreshToken = jwtService.generateRefreshToken(authenticated_existed_user);
          revokeAllUserTokens(authenticated_existed_user);
          saveUserToken(authenticated_existed_user, jwtToken);


            Cookie jwtCookie = new Cookie("refreshToken", refreshToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true); // Set to true in production (requires HTTPS)
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(jwtCookie);

          var responseAuth =  AuthenticationResponse.builder()
                  .accessToken(jwtToken)
                    .username(user.get().getUniqueName())
                  .build();
          return ResponseEntity.ok(responseAuth);

        } else {
          // if the user is new and authenticate with google
          var googleAuthenticatedUser = User.builder()
                  .firstname(name)
                  .lastname("")
                  .email(email)
                  .password(passwordEncoder.encode(idTokenString))
                  .role(Role.FREE_TRIAL_USER)
                  .verified(true)
                  .build();
          var savedUser = repository.save(googleAuthenticatedUser);
          var jwtToken = jwtService.generateToken(googleAuthenticatedUser);
          var refreshToken = jwtService.generateRefreshToken(googleAuthenticatedUser);
          saveUserToken(savedUser, jwtToken);


            Cookie jwtCookie = new Cookie("refreshToken", refreshToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // Set to true in production (requires HTTPS)
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(jwtCookie);


          var responseAuth = AuthenticationResponse.builder()
                  .accessToken(jwtToken)
                  .username(googleAuthenticatedUser.getUniqueName())
                  .build();
          return ResponseEntity.ok(responseAuth);
        }
      }
    } catch (GeneralSecurityException | IOException e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token verification failed");

    }



    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token verification failed");

  }





    /**
     * This method is used to request a reset password for the user.
     * It generates a reset code and sends it to the user's email.
     * @param resetPasswordRequest contains the email of the user
     * @return ResponseEntity with status OK if the code is sent, or FORBIDDEN if the user is not found
     */


  public ResponseEntity<?> requestResetPassword(ResetPasswordRequest resetPasswordRequest) {
    Optional<User> user = repository.findByEmail(resetPasswordRequest.getEmail());
    var user_of_requested_password = user.orElseThrow();
    if(user.isPresent() && user_of_requested_password.isVerified()) {
      String currentTimeInSeconds = String.valueOf(twoFACodeService.getCurrentTimeSeconds());
      final String resetCode = twoFACodeService.generate2FACode()  + currentTimeInSeconds ;
      user_of_requested_password.setResetPassword(resetCode);
      repository.save(user_of_requested_password);
      Map<String, Object> variables = new HashMap<>();
      variables.put("name", user_of_requested_password.getFirstname() + " " + user_of_requested_password.getLastname());
      variables.put("token", resetCode);
      variables.put("confirmUrl", "https://app.flowna.io/reset-password?id=" + java.net.URLEncoder.encode(resetCode, java.nio.charset.StandardCharsets.UTF_8) + "&email=" + user_of_requested_password.getEmail());
      emailService.sendSimpleEmail(user_of_requested_password.getEmail(),
              "Reset your passowrd",
              "emailTemplate",
              variables);
      return ResponseEntity.ok("Code is sent !");
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not found !");

  }

  public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
    Optional<User> user = repository.findByEmail(resetPasswordRequest.getEmail());
    if (user.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
    }
    var requesterUser = user.get();
    final String currentResetCode = requesterUser.getResetPassword();
    if(requesterUser.getResetPassword() != null && currentResetCode.equals(resetPasswordRequest.getResetCode())) {
      final long secondsSinceRequest = twoFACodeService.CalculateDifference(resetPasswordRequest.getResetCode());
      if(secondsSinceRequest > 1800) return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("Timeout! please generate new Request !");
      String password = resetPasswordRequest.getPassword();
      requesterUser.setPassword(passwordEncoder.encode(password));
      requesterUser.setResetPassword(null);
      repository.save(requesterUser);
      return ResponseEntity.ok("Password changed !");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found or link is wrong !");

  }


  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }





  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String refreshToken = null;
      if (request.getCookies() != null) {
          for (Cookie cookie : request.getCookies()) {
              if ("refreshToken".equals(cookie.getName())) {
                  refreshToken = cookie.getValue();
                  break;
              }
          }
      }
      if (refreshToken == null) {
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          return;
      }
      String userEmail = jwtService.extractUsername(refreshToken);
      if (userEmail != null) {
          var user = repository.findByEmail(userEmail).orElseThrow();
          if (jwtService.isTokenValid(refreshToken, user)) {
              var accessToken = jwtService.generateToken(user);
              revokeAllUserTokens(user);
              saveUserToken(user, accessToken);
              var authResponse = AuthenticationResponse.builder()
                      .accessToken(accessToken)
                      .username(user.getFirstname() + " " + user.getLastname())
                      .build();
              new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
          }
      }
  }




}
