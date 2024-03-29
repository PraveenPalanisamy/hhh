package com.accolite.pru.health.AuthApp.service;

import com.accolite.pru.health.AuthApp.exception.ResourceAlreadyInUseException;
import com.accolite.pru.health.AuthApp.exception.ResourceNotFoundException;
import com.accolite.pru.health.AuthApp.model.User;
import com.accolite.pru.health.AuthApp.model.UserDevice;
import com.accolite.pru.health.AuthApp.model.payload.RegistrationRequest;
import com.accolite.pru.health.AuthApp.model.token.EmailVerificationToken;
import com.accolite.pru.health.AuthApp.security.JwtTokenProvider;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class);
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
 
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationTokenService emailVerificationTokenService;

    @Autowired
    public AuthService(UserService userService, JwtTokenProvider tokenProvider,  PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailVerificationTokenService emailVerificationTokenService ) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;

        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailVerificationTokenService = emailVerificationTokenService;
       
    }


    public Optional<User> registerUser(RegistrationRequest newRegistrationRequest) {
        String newRegistrationRequestEmail = newRegistrationRequest.getEmail();
        if (emailAlreadyExists(newRegistrationRequestEmail)) {
            logger.error("Email already exists: " + newRegistrationRequestEmail);
            throw new ResourceAlreadyInUseException("Email", "Address", newRegistrationRequestEmail);
        }
        logger.info("Trying to register new user [" + newRegistrationRequestEmail + "]");
        User newUser = userService.createUser(newRegistrationRequest);
        User registeredNewUser = userService.save(newUser);
        return Optional.ofNullable(registeredNewUser);
    }

  
    public Boolean emailAlreadyExists(String email) {
        return userService.existsByEmail(email);
    }

 
  
    public Optional<User> confirmEmailRegistration(String emailToken) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.findByToken(emailToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Email verification", emailToken));

        User registeredUser = emailVerificationToken.getUser();
        if (registeredUser.getEmailVerified()) {
            logger.info("User [" + emailToken + "] already registered.");
            return Optional.of(registeredUser);
        }

        emailVerificationTokenService.verifyExpiration(emailVerificationToken);
        emailVerificationToken.setConfirmedStatus();
        emailVerificationTokenService.save(emailVerificationToken);

        registeredUser.markVerificationConfirmed();
        userService.save(registeredUser);
        return Optional.of(registeredUser);
    }

    /**
     * Attempt to regenerate a new email verification token given a valid
     * previous expired token. If the previous token is valid, increase its expiry
     * else update the token value and add a new expiration.
     */
    public Optional<EmailVerificationToken> recreateRegistrationToken(String existingToken) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenService.findByToken(existingToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "Existing email verification", existingToken));

        if (emailVerificationToken.getUser().getEmailVerified()) {
            return Optional.empty();
        }
        return Optional.ofNullable(emailVerificationTokenService.updateExistingTokenWithNameAndExpiry(emailVerificationToken));
    }

}


















///**
//* Validates the password of the current logged in user with the given password
//*/
//private Boolean currentPasswordMatches(User currentUser, String password) {
// return passwordEncoder.matches(password, currentUser.getPassword());
//}
//
///**
//* Updates the password of the current logged in user
//*/
//public Optional<User> updatePassword(CustomUserDetails customUserDetails,
//                                  UpdatePasswordRequest updatePasswordRequest) {
// String email = customUserDetails.getEmail();
// User currentUser = userService.findByEmail(email)
//         .orElseThrow(() -> new UpdatePasswordException(email, "No matching user found"));
//
// if (!currentPasswordMatches(currentUser, updatePasswordRequest.getOldPassword())) {
//     logger.info("Current password is invalid for [" + currentUser.getPassword() + "]");
//     throw new UpdatePasswordException(currentUser.getEmail(), "Invalid current password");
// }
// String newPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
// currentUser.setPassword(newPassword);
// userService.save(currentUser);
// return Optional.of(currentUser);
//}
//
///**
//* Generates a JWT token for the validated client
//*/
//public String generateToken(CustomUserDetails customUserDetails) {
// return tokenProvider.generateToken(customUserDetails);
//}
//
///**
//* Generates a JWT token for the validated client by userId
//*/
//private String generateTokenFromUserId(Long userId) {
// return tokenProvider.generateTokenFromUserId(userId);
//}
//
///**
//* Creates and persists the refresh token for the user device. If device exists
//* already, we don't care. Unused devices with expired tokens should be cleaned
//* with a cron job. The generated token would be encapsulated within the jwt.
//* Remove the existing refresh token as the old one should not remain valid.
//*/
//public Optional<RefreshToken> createAndPersistRefreshTokenForDevice(Authentication authentication, LoginRequest loginRequest) {
// User currentUser = (User) authentication.getPrincipal();
// userDeviceService.findByUserId(currentUser.getId())
//         .map(UserDevice::getRefreshToken)
//         .map(RefreshToken::getId)
//         .ifPresent(refreshTokenService::deleteById);
//
// UserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
// RefreshToken refreshToken = refreshTokenService.createRefreshToken();
// userDevice.setUser(currentUser);
// userDevice.setRefreshToken(refreshToken);
// refreshToken.setUserDevice(userDevice);
// refreshToken = refreshTokenService.save(refreshToken);
// return Optional.ofNullable(refreshToken);
//}
//
///**
//* Refresh the expired jwt token using a refresh token and device info. The
//* * refresh token is mapped to a specific device and if it is unexpired, can help
//* * generate a new jwt. If the refresh token is inactive for a device or it is expired,
//* * throw appropriate errors.
//*/
//public Optional<String> refreshJwtToken(TokenRefreshRequest tokenRefreshRequest) {
// String requestRefreshToken = tokenRefreshRequest.getRefreshToken();
//
// return Optional.of(refreshTokenService.findByToken(requestRefreshToken)
//         .map(refreshToken -> {
//             refreshTokenService.verifyExpiration(refreshToken);
//             userDeviceService.verifyRefreshAvailability(refreshToken);
//             refreshTokenService.increaseCount(refreshToken);
//             return refreshToken;
//         })
//         .map(RefreshToken::getUserDevice)
//         .map(UserDevice::getUser)
//         .map(User::getId).map(this::generateTokenFromUserId))
//         .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Missing refresh token in database.Please login again"));
//}
//
///**
//* Generates a password reset token from the given reset request
//*/
//public Optional<PasswordResetToken> generatePasswordResetToken(PasswordResetLinkRequest passwordResetLinkRequest) {
// String email = passwordResetLinkRequest.getEmail();
// return userService.findByEmail(email)
//         .map(user -> {
//             PasswordResetToken passwordResetToken = passwordResetTokenService.createToken();
//             passwordResetToken.setUser(user);
//             passwordResetTokenService.save(passwordResetToken);
//             return Optional.of(passwordResetToken);
//         })
//         .orElseThrow(() -> new PasswordResetLinkException(email, "No matching user found for the given request"));
//}
//
///**
//* Reset a password given a reset request and return the updated user
//*/
//public Optional<User> resetPassword(PasswordResetRequest passwordResetRequest) {
// String token = passwordResetRequest.getToken();
// PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token)
//         .orElseThrow(() -> new ResourceNotFoundException("Password Reset Token", "Token Id", token));
//
// passwordResetTokenService.verifyExpiration(passwordResetToken);
// final String encodedPassword = passwordEncoder.encode(passwordResetRequest.getPassword());
//
// return Optional.of(passwordResetToken)
//         .map(PasswordResetToken::getUser)
//         .map(user -> {
//             user.setPassword(encodedPassword);
//             userService.save(user);
//             return user;
//         });
//}

