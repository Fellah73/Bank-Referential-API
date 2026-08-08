package dz.banquealgerie.referential.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dz.banquealgerie.referential.domain.dto.ApiResponse;
import dz.banquealgerie.referential.domain.dto.auth.LoginDto;
import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.mappers.AuthMapper;
import dz.banquealgerie.referential.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthMapper authMapper;

    // login route
    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            UserEntity userEntity = authMapper.toEntity(loginDto);
            String[] loginData = authService.login(userEntity);

            Cookie cookie = new Cookie("authToken", loginData[0]);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(1 * 60 * 60);

            response.addCookie(cookie);

            ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                    .success(true)
                    .message("Login successful")
                    .statusCode(200)
                    .data(loginData[1])
                    .build();

            return ResponseEntity.ok(apiResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ApiResponse.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .statusCode(401)
                    .build());
        }
    }

    // me route
    @GetMapping(path = "/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        try {
            if (email == null) {
                return ResponseEntity.status(401).body(ApiResponse.<String>builder()
                        .success(false)
                        .message("Unauthorized")
                        .statusCode(401)
                        .build());
            }

            UserEntity user = authService.getMyInfo(email);

            return ResponseEntity.status(200).body(ApiResponse.<UserEntity>builder()
                    .success(true)
                    .message("User found")
                    .statusCode(200)
                    .data(user)
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.<String>builder()
                    .success(false)
                    .message(e.getMessage())
                    .statusCode(404)
                    .build());
        }
    }

    // logout route
    @PostMapping(path = "/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie("authToken", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(0);

            response.addCookie(cookie);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Déconnexion réussie")
                    .statusCode(200)
                    .data(null)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Erreur lors de la déconnexion : " + e.getMessage())
                    .statusCode(500)
                    .data(null)
                    .build());
        }
    }
}
