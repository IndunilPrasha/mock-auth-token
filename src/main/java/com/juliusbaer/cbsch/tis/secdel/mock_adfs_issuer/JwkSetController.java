package com.juliusbaer.cbsch.tis.secdel.mock_adfs_issuer;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwkSetController {

    private final JWKSet jwkSet;

    public JwkSetController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> keys() {
        return jwkSet.toJSONObject();
    }
}
