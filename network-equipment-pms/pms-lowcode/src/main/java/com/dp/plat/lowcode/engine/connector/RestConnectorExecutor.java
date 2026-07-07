package com.dp.plat.lowcode.engine.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * REST 连接器执行器。
 *
 * <p>config JSON 结构：
 * {url, method, headers, body, auth: {type: NONE/BASIC/BEARER/API_KEY, ...},
 *  retry: {maxAttempts, waitMillis}, timeoutMillis, pagination: {type: NONE/OFFSET/PAGE, ...}}</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestConnectorExecutor {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public ConnectorResult execute(String configJson, Map<String, Object> params) {
        try {
            Map<String, Object> config = objectMapper.readValue(configJson, new TypeReference<>() {});
            String url = (String) config.get("url");
            String method = ((String) config.getOrDefault("method", "GET")).toUpperCase();
            Map<String, Object> headers = new HashMap<>((Map<String, Object>) config.getOrDefault("headers", Map.of()));
            Object body = config.get("body");
            applyAuth(config, headers);

            // 重试配置
            Map<String, Object> retryConfig = (Map<String, Object>) config.getOrDefault("retry", Map.of());
            int maxAttempts = ((Number) retryConfig.getOrDefault("maxAttempts", 3)).intValue();
            long waitMillis = ((Number) retryConfig.getOrDefault("waitMillis", 500L)).longValue();

            Retry retry = Retry.of("restConnector", RetryConfig.custom()
                    .maxAttempts(maxAttempts)
                    .waitDuration(Duration.ofMillis(waitMillis))
                    .retryOnResult(r -> ((ConnectorResult) r).getStatus() >= 500)
                    .build());

            ConnectorResult result = retry.executeSupplier(() -> doRequest(url, method, headers, body, params));
            // 分页聚合（简化：仅返回首页，分页类型为 NONE）
            return result;
        } catch (Exception e) {
            log.error("REST 连接器执行失败", e);
            return ConnectorResult.error(500, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ConnectorResult doRequest(String url, String method, Map<String, Object> headers, Object body, Map<String, Object> params) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            headers.forEach((k, v) -> httpHeaders.set(k, String.valueOf(v)));
            HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
            ResponseEntity<Map> resp = restTemplate.exchange(
                    url, HttpMethod.valueOf(method), entity, Map.class);
            return ConnectorResult.builder()
                    .status(resp.getStatusCode().value())
                    .data(resp.getBody())
                    .success(resp.getStatusCode().is2xxSuccessful())
                    .build();
        } catch (Exception e) {
            return ConnectorResult.error(500, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void applyAuth(Map<String, Object> config, Map<String, Object> headers) {
        Map<String, Object> auth = (Map<String, Object>) config.get("auth");
        if (auth == null) {
            return;
        }
        String type = (String) auth.getOrDefault("type", "NONE");
        switch (type) {
            case "BASIC" -> {
                String token = java.util.Base64.getEncoder()
                        .encodeToString(((String) auth.get("credentials")).getBytes());
                headers.put("Authorization", "Basic " + token);
            }
            case "BEARER" -> headers.put("Authorization", "Bearer " + auth.get("token"));
            case "API_KEY" -> headers.put((String) auth.getOrDefault("headerName", "X-API-Key"), auth.get("apiKey"));
            default -> { /* NONE：不做处理 */ }
        }
    }
}
