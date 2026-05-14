package com.kalitron.studio.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class FastApiGateway {

    private final RestClient restClient;

    public FastApiGateway(
        @Value("${app.fastapi.base-url:http://localhost:8000}") String baseUrl,
        @Value("${app.ai-gateway.timeout-seconds:30}") long timeoutSeconds
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    public ChatResponseDTO sendMessage(GatewayChatRequest request) {
        try {
            GatewayChatResponse gatewayResponse = restClient
                .post()
                .uri("/api/v1/chat/message")
                .body(request)
                .retrieve()
                .body(GatewayChatResponse.class);

            if (gatewayResponse == null) {
                throw new FastApiGatewayException("AI Gateway returned an empty chat response");
            }

            ChatResponseDTO response = new ChatResponseDTO();
            response.setSessionId(request.studioSessionId());
            response.setSessionCode(request.sessionCode());
            response.setReply(resolveReply(gatewayResponse));
            response.setSpecsReady(gatewayResponse.specsReady());
            response.setSpecsSummary(null);
            return response;
        } catch (RestClientException e) {
            throw new FastApiGatewayException("AI Gateway is unavailable", e);
        }
    }

    public record GatewayChatRequest(
        @JsonProperty("session_id") String sessionId,
        String message,
        @JsonProperty("image_b64") String imageBase64,
        @JsonIgnore Long studioSessionId,
        @JsonIgnore String sessionCode
    ) {}

    private record GatewayChatResponse(
        @JsonProperty("session_id") String sessionId,
        String reply,
        @JsonProperty("specs_ready") boolean specsReady
    ) {}

    private String resolveReply(GatewayChatResponse gatewayResponse) {
        if (gatewayResponse.reply() != null && !gatewayResponse.reply().isBlank()) {
            return gatewayResponse.reply();
        }
        if (gatewayResponse.specsReady()) {
            return "Perfecto, ya tengo la información base para preparar las especificaciones de diseño.";
        }
        throw new FastApiGatewayException("AI Gateway returned an empty chat response");
    }
}
