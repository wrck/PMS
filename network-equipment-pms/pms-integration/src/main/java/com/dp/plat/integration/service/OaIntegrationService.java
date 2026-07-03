package com.dp.plat.integration.service;

import com.dp.plat.integration.model.oa.OaTodoRequest;

/**
 * OA (致远 OA) integration service.
 *
 * <p>Provides operations to push pending todo items to OA when a Flowable
 * user task is created, and to complete the corresponding OA todo when the
 * Flowable task is completed. All calls are logged to
 * {@link com.dp.plat.integration.entity.IntegrationLog}.</p>
 */
public interface OaIntegrationService {

    /**
     * Obtain an OAuth2 access token (cached until near expiry).
     *
     * @return the access token
     */
    String getAccessToken();

    /**
     * Push a todo item to OA.
     *
     * @param request the todo push request
     * @return {@code true} if the OA API returned a success status
     */
    boolean pushTodo(OaTodoRequest request);

    /**
     * Complete a todo item in OA.
     *
     * @param taskId the OA todo task identifier (the Flowable task id)
     * @return {@code true} if the OA API returned a success status
     */
    boolean completeTodo(String taskId);
}
