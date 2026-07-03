package com.dp.plat.integration.service;

import com.dp.plat.integration.dto.FpHealthDto;
import com.dp.plat.integration.dto.PaymentCallbackDto;
import com.dp.plat.integration.d365.entity.D365Invoice;
import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * FP (Financial Platform) integration service.
 *
 * <p>All methods log to {@link IntegrationLog} with full request/response.
 * Settlement push uses exponential backoff retry (1/2/4/8/16 min, up to 5
 * retry attempts) driven by a {@link java.util.concurrent.ScheduledExecutorService}.</p>
 */
public interface FpIntegrationService {

    /**
     * Obtain an OAuth2 access token (cached until near expiry).
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Push a settlement to FP. The first attempt runs synchronously; on
     * failure, retries are scheduled with exponential backoff (1/2/4/8/16 min,
     * up to 5 retries). Each attempt is logged to {@link IntegrationLog}.
     *
     * @param request the settlement push request
     * @return the FP response from the synchronous first attempt
     */
    FpResponse<String> pushSettlement(SettlementPushRequest request);

    /**
     * Run a single settlement push attempt (used by the retry scheduler and
     * the manual retry path).
     *
     * @param request the settlement push request
     * @return the FP response
     */
    FpResponse<String> pushSettlementOnce(SettlementPushRequest request);

    /**
     * Push an invoice image to the FP OCR API and update the matching
     * {@link D365Invoice} with the recognized fields.
     *
     * @param file the invoice image upload
     * @return the updated D365 invoice
     */
    D365Invoice ocrInvoice(MultipartFile file);

    /**
     * Handle an FP payment callback: update the local settlement's payment
     * status.
     *
     * @param callback the payment callback payload
     */
    void handlePaymentCallback(PaymentCallbackDto callback);

    /**
     * Retry a previously logged FP call by log id. Re-posts the stored request
     * body to the stored request url and updates the log status.
     *
     * @param logId the integration log id
     * @return the refreshed log record
     */
    IntegrationLog retry(Long logId);

    /**
     * Health check for the FP adapter.
     *
     * @return the FP health snapshot
     */
    FpHealthDto healthCheck();
}
