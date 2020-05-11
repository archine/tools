package cn.gjing.tools.auth;

import cn.gjing.tools.auth.exception.TokenAuthorizationException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Token assistant
 *
 * @author Gjing
 **/
@Component
public class TokenAssistant {
    private final AuthorizationInfo authorizationInfo;

    public TokenAssistant(AuthorizationInfo authorizationInfo) {
        this.authorizationInfo = authorizationInfo;
    }

    /**
     * Generate a Token
     *
     * @param value The object that needs to generate the Token
     * @return token
     */
    public String createToken(Map<String, Object> value) {
        try {
            return Jwts.builder()
                    .signWith(this.authorizationInfo.getType(), this.authorizationInfo.getSalt())
                    .addClaims(value)
                    .compact();
        } catch (Exception e) {
            throw new TokenAuthorizationException("Create token error," + e.getMessage());
        }
    }

    /**
     * Token parsing
     *
     * @param token token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(this.authorizationInfo.getSalt())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new TokenAuthorizationException("Invalid token");
        } catch (SignatureException e) {
            throw new TokenAuthorizationException("签名错误");
        } catch (IllegalArgumentException e) {
            throw new TokenAuthorizationException("非法参数异常");
        }
    }
}
