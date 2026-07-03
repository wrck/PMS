package com.dp.plat.integration.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.integration.d365.entity.D365Invoice;
import com.dp.plat.integration.dto.FpHealthDto;
import com.dp.plat.integration.dto.PaymentCallbackDto;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import com.dp.plat.integration.service.FpIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FP (Financial Platform) integration controller: health check, settlement
 * push, invoice OCR and payment callback.
 */
@Tag(name = "FP集成", description = "FP (Financial Platform) integration APIs")
@RestController
@RequestMapping("/api/integration/fp")
@RequiredArgsConstructor
public class FpIntegrationController {

    private final FpIntegrationService fpIntegrationService;

    @Operation(summary = "FP health check")
    @GetMapping("/health")
    public Result<FpHealthDto> health() {
        return Result.ok(fpIntegrationService.healthCheck());
    }

    @Operation(summary = "Manually push a settlement to FP")
    @PostMapping("/push-settlement")
    public Result<FpResponse<String>> pushSettlement(@RequestBody SettlementPushRequest request) {
        return Result.ok(fpIntegrationService.pushSettlement(request));
    }

    @Operation(summary = "Push an invoice image to FP OCR")
    @PostMapping(value = "/ocr-invoice", consumes = "multipart/form-data")
    public Result<D365Invoice> ocrInvoice(@RequestParam("file") MultipartFile file) {
        return Result.ok(fpIntegrationService.ocrInvoice(file));
    }

    @Operation(summary = "Receive an FP payment callback")
    @PostMapping("/payment-callback")
    public Result<Void> paymentCallback(@RequestBody PaymentCallbackDto callback) {
        fpIntegrationService.handlePaymentCallback(callback);
        return Result.ok();
    }
}
