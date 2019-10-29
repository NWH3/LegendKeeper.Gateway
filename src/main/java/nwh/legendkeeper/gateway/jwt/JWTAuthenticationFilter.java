package nwh.legendkeeper.gateway.jwt;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Filter class used to intercept requests and confirm they are valid users
 * against the selected user base
 * 
 * @author Nathanial.Heard
 *
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

	private static Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	
	private long refreshTime;
	
	private String secret;

	private JWTUserRepository userRepository;
	
	public JWTAuthenticationFilter(JWTUserRepository userRepository, long refreshTime, String secret) {
		this.userRepository = userRepository;
		this.secret = secret;
		this.refreshTime = refreshTime;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		Authentication authentication = JWTAuthService.getAuthentication((HttpServletRequest) request, secret);
		
		String token = ((HttpServletRequest)request).getHeader("Authorization");
		if (authentication != null) {
			JWTUser session = userRepository.findOneByJwt(token);
			if (session == null || (session != null && session.isActive() == 0)) {
				((HttpServletResponse) response).setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
			}
			
			try {
				Date sessionLastUpdated = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy").parse(session.getDateUpdated());
				if ((new Date().getTime() - sessionLastUpdated.getTime()) >= refreshTime) {
					((HttpServletResponse) response).setHeader("Authorization", null);
					String refreshedToken = JWTAuthService.addAuthentication((HttpServletResponse) response, session.getUsername(), secret);
					authentication = JWTAuthService.getAuthentication(refreshedToken, secret);
					session.setJwt(refreshedToken);
					session.setDateUpdated(new Date().toString());
					userRepository.save(session);
				}
			} catch (ParseException ex) {
				LOGGER.error("Unable to refresh token due to parse error for date string: " + session.getDateUpdated() + " and error: " + ex.getMessage());
			}
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}
