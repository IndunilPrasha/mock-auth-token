package com.juliusbaer.cbsch.tis.secdel.mock_adfs_issuer;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@RestController
public class MockAdfsIssuerApplication {

	@Autowired
	private RSAKey rsaJwk;

//	private static final String SECRET_KEY = "my-test-secret-12345678901234567890123456789012";
//	private static final Key SIGNING_KEY = new SecretKeySpec(
//			SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());

	public static void main(String[] args) {
		SpringApplication.run(MockAdfsIssuerApplication.class, args);
	}

	@PostMapping("/adfsToken")
	public Map<String, String> generateToken(@RequestBody Map<String, String> credentials) {

		String clientId = credentials.get("clientId");
		String secret = credentials.get("clientSecret");

		if (!"c40eff8e-bb1b-4de9-b9e3-16a4922201f8".equals(clientId) || !"p79CDahgrtfdFoiy6QOYWd3ZT9wyKfCJyHRcb7a9".equals(secret)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}

		try {
			Instant now = Instant.now();
			JWTClaimsSet claims = new JWTClaimsSet.Builder()
					.jwtID(UUID.randomUUID().toString())
					.subject("sdw-user")
					.issuer("http://localhost:8081")
					.audience("modern-auth")
					.claim("client_id", clientId)
					.issueTime(Date.from(now))
					.expirationTime(Date.from(now.plus(10, ChronoUnit.MINUTES)))
					.build();

			JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
					.keyID(rsaJwk.getKeyID())
					.type(JOSEObjectType.JWT)
					.build();

			SignedJWT jwt = new SignedJWT(header, claims);
			RSASSASigner signer = new RSASSASigner(rsaJwk.toPrivateKey());
			jwt.sign(signer);

			return Map.of("access_token", jwt.serialize(), "token_type", "Bearer");
		} catch (Exception e) {
			throw new RuntimeException("Token creation failed", e);
		}
	}

}
