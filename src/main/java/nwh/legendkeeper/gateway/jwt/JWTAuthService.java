package nwh.legendkeeper.gateway.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security
            .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

/**
 * Service is used to contain all
 * logic for JWT authentication and 
 * token header assignment
 * 
 * @author Nathanial.Heard
 *
 */
public class JWTAuthService {
    
    private static final long EXPIRATION_TIME = 1_800_000; // 30 minutes
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    public static String addAuthentication(HttpServletResponse response, String username, String secret) {
    	String JWT = Jwts.builder()
						.setSubject(username)
						.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
						.signWith(SignatureAlgorithm.HS512, secret)
						.compact();
    	String token = TOKEN_PREFIX + " " + JWT;
    	response.addHeader(HEADER_STRING, token);
    	return token;
    }
    
    public static Authentication getAuthentication(HttpServletRequest request, String secret) {
		String token = request.getHeader(HEADER_STRING);
		return getAuthentication(token, secret);
    }

    public static Authentication getAuthentication(String token, String secret) {
		UsernamePasswordAuthenticationToken authToken = null;
		if (token != null) {
		  String user = Jwts.parser()
		      .setSigningKey(secret)
		      .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
		      .getBody()
		      .getSubject();
		
		  if (user != null) {
			  authToken = new UsernamePasswordAuthenticationToken(user, null, emptyList());
		  }
		}
		return authToken;
    }
}