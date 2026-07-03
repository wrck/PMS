package com.dp.plat.integration.constant;

/**
 * Constants for the integration module: log types, business types and response
 * statuses.
 */
public final class IntegrationConstants {

    private IntegrationConstants() {
    }

    /** Log type: Microsoft Dynamics 365. */
    public static final String LOG_TYPE_D365 = "D365";
    /** Log type: Financial Platform. */
    public static final String LOG_TYPE_FP = "FP";
    /** Log type: OA system. */
    public static final String LOG_TYPE_OA = "OA";
    /** Log type: SMS system. */
    public static final String LOG_TYPE_SMS = "SMS";
    /** Log type: EHR system. */
    public static final String LOG_TYPE_EHR = "EHR";

    /** Business type: D365 purchase order push. */
    public static final String BIZ_PURCHASE_ORDER = "PURCHASE_ORDER";
    /** Business type: D365 purchase receipt push. */
    public static final String BIZ_PURCHASE_RECEIPT = "PURCHASE_RECEIPT";
    /** Business type: FP settlement push. */
    public static final String BIZ_SETTLEMENT = "SETTLEMENT";
    /** Business type: invoice. */
    public static final String BIZ_INVOICE = "INVOICE";
    /** Business type: OA todo push. */
    public static final String BIZ_OA_TODO_PUSH = "TODO_PUSH";
    /** Business type: OA todo complete. */
    public static final String BIZ_OA_TODO_COMPLETE = "TODO_COMPLETE";

    /** Response status: success. */
    public static final String STATUS_SUCCESS = "SUCCESS";
    /** Response status: failed. */
    public static final String STATUS_FAILED = "FAILED";
    /** Response status: pending. */
    public static final String STATUS_PENDING = "PENDING";
}
