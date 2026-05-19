package com.kalitron.studio.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalitron.studio.service.dto.ChatResponseDTO;
import com.kalitron.studio.service.dto.VisualConceptResponseDTO;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class FastApiGateway {

    private final String baseUrl;
    private final RestClient restClient;

    public FastApiGateway(
        @Value("${app.fastapi.base-url:http://localhost:8000}") String baseUrl,
        @Value("${app.ai-gateway.timeout-seconds:30}") long timeoutSeconds
    ) {
        this.baseUrl = baseUrl;
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

    public VisualConceptResponseDTO generateVisualConcept(GatewayGenerateRequest request) {
        try {
            GatewayGenerateResponse gatewayResponse = restClient
                .post()
                .uri("/api/v1/images/generate")
                .body(request)
                .retrieve()
                .body(GatewayGenerateResponse.class);

            if (gatewayResponse == null || gatewayResponse.imageUrl() == null || gatewayResponse.imageUrl().isBlank()) {
                throw new FastApiGatewayException("AI Gateway returned an empty visual concept response");
            }

            VisualConceptResponseDTO response = new VisualConceptResponseDTO();
            response.setSessionId(request.studioSessionId());
            response.setSessionCode(request.sessionCode());
            response.setImageUrl(resolveGatewayUrl(gatewayResponse.imageUrl()));
            response.setPromptUsed(gatewayResponse.promptUsed());
            response.setPipeline(gatewayResponse.pipeline());
            response.setBadge("img2img".equals(gatewayResponse.pipeline()) ? "Based on your photo" : "Generated from description");
            return response;
        } catch (RestClientException e) {
            throw new FastApiGatewayException("AI Gateway image generation is unavailable", e);
        }
    }

    public record GatewayChatRequest(
        @JsonProperty("session_id") String sessionId,
        String message,
        @JsonProperty("image_b64") String imageBase64,
        @JsonProperty("image_mime_type") String imageMimeType,
        @JsonIgnore Long studioSessionId,
        @JsonIgnore String sessionCode
    ) {}

    private record GatewayChatResponse(
        @JsonProperty("session_id") String sessionId,
        String reply,
        @JsonProperty("specs_ready") boolean specsReady
    ) {}

    public record GatewayGenerateRequest(
        @JsonProperty("session_id") String sessionId,
        @JsonProperty("client_image_b64") String clientImageBase64,
        String style,
        String layout,
        String finish,
        @JsonProperty("project_type") String projectType,
        @JsonProperty("design_brief") String designBrief,
        @JsonIgnore Long studioSessionId,
        @JsonIgnore String sessionCode
    ) {}

    private record GatewayGenerateResponse(
        @JsonProperty("session_id") String sessionId,
        @JsonProperty("image_url") String imageUrl,
        @JsonProperty("prompt_used") String promptUsed,
        String pipeline
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

    private String resolveGatewayUrl(String url) {
        URI uri = URI.create(url);
        if (uri.isAbsolute()) {
            return url;
        }
        return URI.create(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/").resolve(url.startsWith("/") ? url.substring(1) : url).toString();
    }
}
