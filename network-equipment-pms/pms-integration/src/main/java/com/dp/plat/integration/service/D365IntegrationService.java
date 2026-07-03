package com.dp.plat.integration.service;

import com.dp.plat.integration.entity.IntegrationLog;
import com.dp.plat.integration.model.d365.PurchaseHeader;
import com.dp.plat.integration.model.d365.PurchaseReceiptHeader;

/**
 * D365 (Microsoft Dynamics 365) integration service.
 *
 * <p>All methods log to {@link IntegrationLog} and support automatic retry.</p>
 */
public interface D365IntegrationService {

    /**
     * Obtain an OAuth2 access token (cached until near expiry).
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Push a purchase receipt to D365.
     *
     * @param header the purchase receipt header
     * @return the D365 response body
     */
    String pushPurchaseReceipt(PurchaseReceiptHeader header);

    /**
     * Push a purchase order to D365.
     *
     * @param header the purchase order header
     * @return the D365 response body
     */
    String pushPurchaseOrder(PurchaseHeader header);

    /**
     * Retry a previously logged D365 call by log id. Re-posts the stored
     * request body to the stored request url and updates the log status.
     *
     * @param logId the integration log id
     * @return the refreshed log record
     */
    IntegrationLog retry(Long logId);
}
