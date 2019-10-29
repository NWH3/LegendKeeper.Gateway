package nwh.legendkeeper.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gateway Controller used to expose all
 * APIs routes with authentication 
 * and authorization
 * 
 * @author Nathanial.Heard
 *
 */
@RestController
public class GatewayController {

	@Autowired
	private GatewayService gatewayService;
	
	/**
	 * Expose public key to encrypt
	 * sensitive information for login
	 * 
	 * @return the RSA public key
	 */
	@GetMapping("/key")
    public ResponseEntity<String> getPublicKey() {
		String publicKey = gatewayService.getPublicKey();
		return new ResponseEntity<String>(publicKey, HttpStatus.OK);
    }
	
	/**
	 * Expose user logout to deactivate tokens
	 * 
	 * @param authorization, the active auth token to deactivate 
	 * @return success or failure message
	 */
	@GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization") String auth) {
		String result = gatewayService.logout(auth);
		return new ResponseEntity<String>(result, HttpStatus.OK);
    }
}
