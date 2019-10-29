package nwh.legendkeeper.gateway.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import nwh.legendkeeper.gateway.crypto.RSACrypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;

/**
 * Filter class used to authenticate 
 * given credentials
 * 
 * @author Nathanial.Heard
 *
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private static Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);
	private static String GROUP = "Group";
	
	private String secret;
	
	private JWTUserRepository userRepository;

	public JWTLoginFilter(String url, AuthenticationManager authManager, JWTUserRepository userRepository, String secret) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authManager);
		this.userRepository = userRepository;
		this.secret = secret;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
		AccountLoginDto creds = null;
		String username = "";
		boolean isAuthenticated = false;
		
		try {
			creds = new ObjectMapper().readValue(req.getInputStream(), AccountLoginDto.class);
			if (creds != null && creds.getSecret() != null) {
				RSACrypto ac = new RSACrypto();
				PrivateKey privateKey = ac.getPrivateKey();
				
				String jsonObj = ac.decrypt(creds.getSecret(), privateKey);
				creds = new AccountLoginDto(jsonObj);
				username = creds.getUsername();
				JWTUser user = userRepository.findOneByUsername(username);
				if (user != null) {
					BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
					isAuthenticated = encoder.matches(creds.getPassword() +  user.getSalt(), user.getPassword());
					String permissionMappings = user.getPermissions().toString();
					res.addHeader(GROUP, permissionMappings);
				} else {
					LOGGER.error("Unable to find username provided for: " + username);
				}
			} else {
				LOGGER.error("Unable to retrieve credentials from request...");
			}
		} catch (InvalidKeyException ex) {
			LOGGER.error("Invalid Key Exception with message: " + ex.getMessage());
		} catch (IllegalBlockSizeException ex) {
			LOGGER.error("Illegal Block Size Exception with message: " + ex.getMessage());
		} catch (BadPaddingException ex) {
			LOGGER.error("Bad Padding Exception with message: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("No Such Algorithm Exception with message: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			LOGGER.error("Invalid Key Provided with message: " + ex.getMessage());
		} catch (InvalidKeySpecException ex) {
			LOGGER.error("Invalid Key Provided with message: " + ex.getMessage());
		} catch (IOException ex) {
			LOGGER.error("IOException occurred with message: " + ex.getMessage());
		}
		Authentication auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
		if (!isAuthenticated) {
			auth = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
		}
		
		return auth;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		String token = JWTAuthService.addAuthentication(res, auth.getName(), secret);
		
		if (auth.isAuthenticated()) {
			JWTUser user = userRepository.findOneByUsername(auth.getName());
			if (user != null) {
				user.setJwt(token);
				user.setActive(1);
				user.setDateUpdated(new Date().toString());
				userRepository.save(user);
			} else {
				LOGGER.error("No user found with given name: " + auth.getName());
			}
		}
	}
	
}