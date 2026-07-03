package com.dp.plat.mock.fp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Mock FP REST endpoints: OAuth2 token, settlement push, invoice OCR and
 * payment callback. Returns canned JSON.
 */
@RestController
@RequestMapping
public class MockFpController {

    /** OAuth2 token endpoint returning a fake JWT with 1h expiry. */
    @PostMapping(value = "/oauth2/token",
            consumes = "application/x-www-form-urlencoded",
            produces = "application/json")
    public ResponseEntity<Map<String, Object>> token() {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 3600L;
        String header = base64("{\"alg\":\"none\",\"typ\":\"JWT\"}");
        String payload = base64("{\"sub\":\"mock-fp\",\"iat\":" + now + ",\"exp\":" + exp + "}");
        String token = header + "." + payload + ".mock-signature";
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600));
    }

    /** Accepts a settlement push and returns success. */
    @PostMapping("/api/settlements/push")
    public ResponseEntity<Map<String, Object>> pushSettlement(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "code", "0",
                "message", "success",
                "data", Map.of("fpSettlementId", "FP-STL-" + System.currentTimeMillis())));
    }

    /** Trivial base URL hit used by the health check. */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiRoot() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "mock-fp"));
    }

    /**
     * Mock invoice OCR endpoint. Accepts a multipart file upload and returns
     * canned OCR recognition results.
     */
    @PostMapping("/api/ocr/invoice")
    public ResponseEntity<Map<String, Object>> ocrInvoice(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of(
                "code", "0",
                "message", "success",
                "data", Map.of(
                        "invoiceNo", "INV-OCR-" + System.currentTimeMillis() % 100000,
                        "amount", 9800.00,
                        "taxAmount", 1274.00,
                        "totalAmount", 11074.00,
                        "vendorName", "MockOCRVendor")));
    }

    /** Accepts a payment callback status and returns success. */
    @PostMapping("/api/payment-callback")
    public ResponseEntity<Map<String, Object>> paymentCallback(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("code", "0", "message", "success"));
    }
}
