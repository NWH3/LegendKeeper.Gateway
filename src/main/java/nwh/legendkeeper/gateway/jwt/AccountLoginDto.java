package nwh.legendkeeper.gateway.jwt;

import org.json.JSONObject;

/**
 * Class used to contain the login data used for either encrypted login or
 * non-encrypted login
 * 
 * username & password are used for none encrypted
 * 
 * secret is used for encrypted and the config property should be set to allow
 * the path
 * 
 * @author Nathanial.Heard
 *
 */
public class AccountLoginDto {

	private String secret;

	private String username;

	private String password;

	public AccountLoginDto() {
		// Default
	}

	public AccountLoginDto(String jsonStr) {
		JSONObject jsonObj = new JSONObject(jsonStr);
		if (jsonObj != null && jsonObj.has("username") && jsonObj.has("password")) {
			this.username = jsonObj.getString("username");
			this.password = jsonObj.getString("password");
		}
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
