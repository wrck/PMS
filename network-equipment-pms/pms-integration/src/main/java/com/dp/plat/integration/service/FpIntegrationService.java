package com.dp.plat.integration.service;

import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.fp.FpResponse;
import com.dp.plat.integration.model.fp.SettlementPushRequest;

/**
 * FP (Financial Platform) integration service.
 *
 * <p>All methods log to {@link IntegrationLog} and support automatic retry.</p>
 */
public interface FpIntegrationService {

    /**
     * Obtain an OAuth2 access token (cached until near expiry).
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Push a settlement to FP.
     *
     * @param request the settlement push request
     * @return the FP response
     */
    FpResponse<String> pushSettlement(SettlementPushRequest request);

    /**
     * Retry a previously logged FP call by log id. Re-posts the stored request
     * body to the stored request url and updates the log status.
     *
     * @param logId the integration log id
     * @return the refreshed log record
     */
    IntegrationLog retry(Long logId);
}
