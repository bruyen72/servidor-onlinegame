package quironconcursos.services.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${app.security.token-jwt.access-token.secret-key}")
    private String secretKeyAccessToken;

    @Value("${app.security.token-jwt.access-token.expire-length}")
    private Long validityInMilliSecondsAccessToken;

    @Value("${app.security.token-jwt.ticket-token.secret-key}")
    private String secretKeyTicketToken;

    @Value("${app.security.token-jwt.ticket-token.expire-length}")
    private Long validityInMilliSecondsTicketToken;

    @Value("${app.security.token-jwt.reset-password-token.secret-key}")
    private String secretKeyResetPasswordToken;

    @Value("${app.security.token-jwt.reset-password-token.expire-length}")
    private Long validityInMilliSecondsResetPasswordToken;

    public String createAccessToken(String username, String role) {
        return this.createToken(username, role, secretKeyAccessToken, validityInMilliSecondsAccessToken);
    }

    public String createTicketToken(String username, String role) {
        return this.createToken(username, role, secretKeyTicketToken, validityInMilliSecondsTicketToken);
    }

    public String createResetPasswordToken(String username, String role) {
        return this.createToken(username, role, secretKeyResetPasswordToken, validityInMilliSecondsResetPasswordToken);
    }

    public boolean validateAccessToken(String token) {
        return this.validateToken(token, secretKeyAccessToken);
    }

    public boolean validateTicketToken(String token) {
        return this.validateToken(token, secretKeyTicketToken);
    }

    public boolean validateResetPasswordToken(String token) {
        return this.validateToken(token, secretKeyResetPasswordToken);
    }

    public String getUsername(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);

        return decodedJWT.getSubject();
    }

    private String getIssueURL() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    private String createToken(String username, String role, String secretKey, Long validityInMilliSeconds) {
        Instant now = Instant.now();
        Instant validity = now.plusMillis(validityInMilliSeconds);

        return JWT.create()
                .withClaim("role", role)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(validity))
                .withSubject(username)
                .withIssuer(this.getIssueURL())
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC512(secretKey.getBytes(StandardCharsets.UTF_8)))
                .strip();
    }

    private boolean validateToken(String token, String secretKey) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey.getBytes(StandardCharsets.UTF_8));

            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT.getExpiresAt().before(Date.from(Instant.now()))) {
                return false;
            }

            return !decodedJWT.getClaim("role").isNull();
        } catch (Exception e) {
            return false;
        }
    }

}
