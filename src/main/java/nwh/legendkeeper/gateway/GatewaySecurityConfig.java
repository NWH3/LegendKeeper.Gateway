package nwh.legendkeeper.gateway;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import nwh.legendkeeper.gateway.jwt.JWTAuthenticationFilter;
import nwh.legendkeeper.gateway.jwt.JWTLoginFilter;
import nwh.legendkeeper.gateway.jwt.JWTUserRepository;


/**
 * LDAP Security Configuration class used to contain and control all LDAP
 * settings. The LDAPTemplate and LDAPContext are used by other classes to
 * gather group attribute information about the logged in user
 * 
 * @author Nathanial.Heard
 *
 */
@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${jwt.secret}")
	private String SECRET;
	
	@Value("${refresh.time}")
	private long refreshTime;
	
	private static final String LOGIN_URL = "/login";
	
	private static final String LOGOUT_URL = "/logout";
	
	private static final String KEY_URL = "/key";

	@Autowired
	private JWTUserRepository userRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.logout().disable();
		
		http.csrf().disable()
				.cors().configurationSource(corsConfigurationSource())
			.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, LOGIN_URL).permitAll()
				.antMatchers(HttpMethod.GET, LOGOUT_URL).permitAll()
				.antMatchers(HttpMethod.GET, KEY_URL).permitAll()
		        .anyRequest().fullyAuthenticated()
			.and()
		        .addFilterBefore(new JWTLoginFilter(LOGIN_URL, authenticationManager(), userRepository, SECRET),
		                UsernamePasswordAuthenticationFilter.class)
		        .addFilterBefore(new JWTAuthenticationFilter(userRepository, refreshTime, SECRET),
		                UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin","Accept","X-Requested-With","Content-Type","Access-Control-Request-Method","Access-Control-Allow-Headers","Access-Control-Request-Headers","Authorization"));
        configuration.setExposedHeaders(Arrays.asList("Origin","Accept","X-Requested-With","Content-Type","Access-Control-Request-Method","Access-Control-Allow-Headers","Access-Control-Request-Headers","Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}