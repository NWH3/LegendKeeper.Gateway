package nwh.legendkeeper.gateway.jwt;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class JWTUser {

	@Id
	private String id;
	
	private String jwt;
	
	private String username;
	
	private String password;
	
	private String salt;
	
	private List<String> permissions;
	
	private Integer isActive;
	
	private String dateCreated;
	
	private String dateUpdated;
	
	public JWTUser() {
		// Default
		this.isActive = 0;
		this.dateCreated = new Date().toString();
		this.dateUpdated = new Date().toString();
	}
	
	public JWTUser(String jwt, String username) {
		this.jwt = jwt;
		this.username = username;
		this.isActive = 0;
		this.dateCreated = new Date().toString();
		this.dateUpdated = new Date().toString();
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer isActive() {
		return isActive;
	}

	public void setActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
}
