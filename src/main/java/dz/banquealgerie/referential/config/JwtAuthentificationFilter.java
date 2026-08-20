package dz.banquealgerie.referential.config;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.repositories.auth.AuthRepository;
import dz.banquealgerie.referential.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthentificationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/auth/login")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null) {
            try {
                if (jwtService.isTokenValid(token)) {
                    String email = jwtService.extractEmail(token);

                    UserEntity currentUser = userRepository.findAll().stream()
                            .filter(u -> email.equals(u.getEmail()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));

                    request.setAttribute("userEmail", email);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    writeErrorResponse(response, "expired jwt", 401);
                    return;
                }
            } catch (Exception e) {
                writeErrorResponse(response, "Auth error : " + e.getMessage(), 401);
                return;
            }
        } else {
            writeErrorResponse(response, "No auth token provided", 401);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        String jsonResponse = String.format(
                "{\"success\": false, \"message\": \"%s\", \"statusCode\": %d, \"data\": null}",
                message, statusCode);

        response.getWriter().write(jsonResponse);
    }
}
