package com.bisa.sis.auth;

import com.bisa.sis.audit.AuditService;
import com.bisa.sis.auth.dto.AuthResponse;
import com.bisa.sis.auth.dto.LoginRequest;
import com.bisa.sis.security.JwtService;
import com.bisa.sis.security.SecurityUser;
import com.bisa.sis.user.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@EnableConfigurationProperties(CookieProperties.class)
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/auth";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieProperties cookieProperties;
    private final AuditService auditService;

    public AuthController(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           RefreshTokenService refreshTokenService,
                           CookieProperties cookieProperties,
                           AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieProperties = cookieProperties;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User user = securityUser.getUser();

        String accessToken = jwtService.generateAccessToken(securityUser);
        String refreshToken = refreshTokenService.issue(user);
        setRefreshCookie(response, refreshToken, refreshTokenService.getTtlSeconds());

        auditService.record(user.getId(), "LOGIN_SUCCESS", "User", user.getId().toString(), null);

        return toAuthResponse(accessToken, user);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
                                 HttpServletResponse response) {
        if (refreshToken == null) {
            throw new BadCredentialsException("Missing refresh token");
        }

        RefreshTokenService.RotatedToken rotated;
        try {
            rotated = refreshTokenService.rotate(refreshToken);
        } catch (RefreshTokenService.InvalidRefreshTokenException ex) {
            clearRefreshCookie(response);
            throw new BadCredentialsException("Refresh token is invalid, expired, or revoked", ex);
        }

        User user = rotated.user();
        if (!user.isEnabled()) {
            clearRefreshCookie(response);
            throw new BadCredentialsException("Account is disabled");
        }

        setRefreshCookie(response, rotated.rawToken(), refreshTokenService.getTtlSeconds());
        String accessToken = jwtService.generateAccessToken(new SecurityUser(user));

        return toAuthResponse(accessToken, user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken,
                        HttpServletResponse response) {
        if (refreshToken != null) {
            refreshTokenService.revoke(refreshToken);
        }
        clearRefreshCookie(response);
    }

    private AuthResponse toAuthResponse(String accessToken, User user) {
        return new AuthResponse(
                accessToken,
                jwtService.getAccessTokenTtlSeconds(),
                new AuthResponse.UserSummary(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole())
        );
    }

    private void setRefreshCookie(HttpServletResponse response, String value, long ttlSeconds) {
        response.addHeader("Set-Cookie", buildCookie(value, ttlSeconds).toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", buildCookie("", 0).toString());
    }

    private ResponseCookie buildCookie(String value, long ttlSeconds) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .sameSite(cookieProperties.getSameSite())
                .path(REFRESH_COOKIE_PATH)
                .maxAge(ttlSeconds)
                .build();
    }
}
