package com.dp.plat.mock.oa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Mock Seeyon OA REST endpoints: OAuth2 token, todo push/complete/transfer.
 * Returns canned JSON.
 */
@RestController
@RequestMapping
public class MockOaController {

    /** OAuth2 token endpoint returning a fake JWT with 1h expiry. */
    @PostMapping(value = "/oauth2/token",
            consumes = "application/x-www-form-urlencoded",
            produces = "application/json")
    public ResponseEntity<Map<String, Object>> token() {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 3600L;
        String header = base64("{\"alg\":\"none\",\"typ\":\"JWT\"}");
        String payload = base64("{\"sub\":\"mock-oa\",\"iat\":" + now + ",\"exp\":" + exp + "}");
        String token = header + "." + payload + ".mock-signature";
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600));
    }

    /** Accepts a todo push and returns success. */
    @PostMapping("/api/todo/push")
    public ResponseEntity<Map<String, Object>> pushTodo(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "code", "0",
                "message", "success",
                "data", Map.of("oaTodoId", "OA-TODO-" + System.currentTimeMillis())));
    }

    /** Marks a todo as complete. */
    @PutMapping("/api/todo/complete")
    public ResponseEntity<Map<String, Object>> completeTodo(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("code", "0", "message", "success"));
    }

    /** Transfers a todo to a new handler. */
    @PutMapping("/api/todo/transfer")
    public ResponseEntity<Map<String, Object>> transferTask(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("code", "0", "message", "success"));
    }

    /** Trivial base URL hit used by the health check. */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiRoot() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "mock-oa"));
    }
}
