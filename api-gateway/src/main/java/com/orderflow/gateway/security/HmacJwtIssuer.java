package com.orderflow.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class HmacJwtIssuer {
    public static final Base64.Encoder BASE64_URL_NO_PAD = Base64.getUrlEncoder().withoutPadding();
    public static final long TOKEN_TTL_IN_SECONDS = 3600L;
    public static final String ENCRYPTION_ALGORITHM = "HmacSHA256";

    private final byte[] secretKeyBytes;
    private final ObjectMapper objectMapper;

    public HmacJwtIssuer(@Value("${security.jwt.secret}") String secret ,ObjectMapper objectMapper) {
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.objectMapper = objectMapper;
    }

    public String issue(String subject,String role) {
        Instant now = Instant.now();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", subject);
        payload.put("roles",List.of(role));
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.getEpochSecond() + TOKEN_TTL_IN_SECONDS);

        try {
            String encodedHeader = BASE64_URL_NO_PAD.encodeToString(objectMapper.writeValueAsBytes(header));
            String encodedPayload = BASE64_URL_NO_PAD.encodeToString(objectMapper.writeValueAsBytes(payload));
            String signingInput = encodedHeader + "." + encodedPayload;
            Mac mac = Mac.getInstance(ENCRYPTION_ALGORITHM);
            mac.init(new SecretKeySpec(secretKeyBytes, ENCRYPTION_ALGORITHM));
            String encodedSignature = BASE64_URL_NO_PAD.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

            return signingInput + "." + encodedSignature;
        } catch(JsonProcessingException |GeneralSecurityException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
    }
}
