package gateway.controller;

import gateway.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.username());

        return authService.login(request.username(), request.password())
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                log.warn("Login failed: {}", e.getMessage());
                return Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials")));
            });
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<Map<String, Object>>> refresh(@RequestBody RefreshRequest request) {
        log.debug("Token refresh attempt");

        return authService.refresh(request.refreshToken())
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                log.warn("Token refresh failed: {}", e.getMessage());
                return Mono.just(ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token")));
            });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(@RequestBody RefreshRequest request) {
        log.info("Logout attempt");

        return authService.logout(request.refreshToken())
            .then(Mono.just(ResponseEntity.ok(Map.of("message", "Logged out successfully"))))
            .onErrorResume(e -> {
                log.warn("Logout failed: {}", e.getMessage());
                return Mono.just(ResponseEntity.ok(Map.of("message", "Logged out")));
            });
    }

    public record LoginRequest(String username, String password) {}
    public record RefreshRequest(String refreshToken) {}
}
