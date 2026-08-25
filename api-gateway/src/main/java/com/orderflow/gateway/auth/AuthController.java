package com.orderflow.gateway.auth;

import com.orderflow.gateway.security.HmacJwtIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final HmacJwtIssuer jwtIssuer;

    @PostMapping("/token")
    public TokenResponse issueToken(@RequestBody TokenRequest request) {
        if(request.customerId() == null || request.customerId().isBlank() || request.role() == null || request.role().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerId and role are both required");
        }

        return new TokenResponse(jwtIssuer.issue(request.customerId(), request.role()));
    }
}
