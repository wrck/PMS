package com.dp.plat.mock.d365;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Mock D365 REST endpoints: OAuth2 token, purchase orders, purchase receipts,
 * invoices and asset serial numbers. Returns canned JSON.
 */
@RestController
@RequestMapping
public class MockD365Controller {

    /**
     * OAuth2 client_credentials token endpoint. Returns a fake JWT with a
     * 1-hour expiry.
     */
    @PostMapping(value = "/oauth2/token",
            consumes = "application/x-www-form-urlencoded",
            produces = "application/json")
    public ResponseEntity<Map<String, Object>> token() {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 3600L;
        String header = base64("{\"alg\":\"none\",\"typ\":\"JWT\"}");
        String payload = base64("{\"sub\":\"mock-d365\",\"iat\":" + now + ",\"exp\":" + exp + "}");
        String token = header + "." + payload + ".mock-signature";
        return ResponseEntity.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600));
    }

    /** Sample purchase order list. */
    @GetMapping("/api/purchase-orders")
    public ResponseEntity<Map<String, Object>> purchaseOrders() {
        return ResponseEntity.ok(Map.of("value", List.of(
                Map.of("purchaseOrderId", "PO-2024-001",
                        "vendorAccountNumber", "VENDOR-001",
                        "lines", List.of()),
                Map.of("purchaseOrderId", "PO-2024-002",
                        "vendorAccountNumber", "VENDOR-002",
                        "lines", List.of()))));
    }

    /** Accepts a purchase receipt push and echoes a fake D365 receipt id. */
    @PostMapping("/api/purchase-receipts")
    public ResponseEntity<Map<String, Object>> pushReceipt(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
                "code", "200",
                "message", "success",
                "d365ReceiptId", "RCPT-" + System.currentTimeMillis()));
    }

    /** Sample purchase receipts for the sync flow. */
    @GetMapping("/api/purchase-receipts")
    public ResponseEntity<Map<String, Object>> purchaseReceipts() {
        return ResponseEntity.ok(Map.of("value", List.of(
                Map.of("receiptNo", "RCPT-001",
                        "poNo", "PO-2024-001",
                        "sn", "SN-A001",
                        "d365ReceiptId", "D365-RCPT-001"))));
    }

    /** Sample invoices for the sync flow. */
    @GetMapping("/api/invoices")
    public ResponseEntity<Map<String, Object>> invoices() {
        return ResponseEntity.ok(Map.of("value", List.of(
                Map.of("invoiceNo", "INV-2024-001",
                        "settlementNo", "STL-2024-001",
                        "amount", 10000.00,
                        "taxAmount", 1300.00,
                        "totalAmount", 11300.00,
                        "vendorName", "MockVendor",
                        "d365InvoiceId", "D365-INV-001"))));
    }

    /** Sample asset serial numbers for the sync flow. */
    @GetMapping("/api/asset-serial-numbers")
    public ResponseEntity<Map<String, Object>> assetSerialNumbers() {
        return ResponseEntity.ok(Map.of("value", List.of(
                Map.of("assetId", 1, "sn", "SN-A001"),
                Map.of("assetId", 2, "sn", "SN-A002"))));
    }

    /** Trivial base URL hit used by the health check. */
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiRoot() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "mock-d365"));
    }

    private static String base64(String raw) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
