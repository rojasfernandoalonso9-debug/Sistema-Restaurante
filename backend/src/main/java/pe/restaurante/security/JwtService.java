package pe.restaurante.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service public class JwtService {
    private final String secret; private final long expiration;
    public JwtService(@Value("${app.jwt.secret:}") String secret,@Value("${app.jwt.expiration-ms:86400000}") long expiration){this.secret=secret;this.expiration=expiration;}
    private SecretKey key(){if(secret.length()<32)throw new IllegalStateException("JWT_SECRET debe contener al menos 32 caracteres");return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
    public String issue(String email){return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration)).signWith(key()).compact();}
    public String subject(String token){return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();}
}
