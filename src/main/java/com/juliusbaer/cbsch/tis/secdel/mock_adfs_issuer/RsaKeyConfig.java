package com.juliusbaer.cbsch.tis.secdel.mock_adfs_issuer;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class RsaKeyConfig {

    private final RSAKey rsaJwk;

    public RsaKeyConfig() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair keyPair = gen.generateKeyPair();

            rsaJwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey(keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate RSA key", e);
        }
    }

    @Bean
    public RSAKey rsaJwk() {
        return rsaJwk;
    }

    @Bean
    public JWKSet jwkSet() {
        return new JWKSet(rsaJwk);
    }
}
