package nwh.legendkeeper.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nwh.legendkeeper.gateway.crypto.RSACrypto;
import nwh.legendkeeper.gateway.jwt.JWTUser;
import nwh.legendkeeper.gateway.jwt.JWTUserRepository;

/**
 * Service used to contain all logic
 * for the gateway service web APIs
 * 
 * @author Nathanial.Heard
 *
 */
@Service
public class GatewayService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayService.class);
	
	public Gson gson;
	
	@Autowired
	private JWTUserRepository userRepository;
	
	public GatewayService() {
		gson = new Gson();
	}
	
	public String getPublicKey() {
		String publicKeyStr = null;
		try {
			RSACrypto ac = new RSACrypto();
			PublicKey publicKey = ac.getPublicKey();
			publicKeyStr = publicKey.toString();
			KeyFactory fact = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec spec = fact.getKeySpec(publicKey,
		            X509EncodedKeySpec.class);
			publicKeyStr = new String(Base64.getEncoder().encode(spec.getEncoded()));
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("No Such Algorithm Exception with message: " + ex.getMessage());
		} catch (IOException ex) {
			LOGGER.error("IO Exception with message: " + ex.getMessage());
		} catch (InvalidKeySpecException ex) {
			LOGGER.error("Invalid Key Exception with message: " + ex.getMessage());
		} catch (NoSuchPaddingException ex) {
			LOGGER.error("No Such Padding Exception with message: " + ex.getMessage());
		}
		
		return publicKeyStr;
    }
	
	public String logout(String authorization) {
		String result = "Successfully logged out!";
		JWTUser foundModel = userRepository.findOneByJwt(authorization);
		if (foundModel != null) {
			foundModel.setActive(0);
			foundModel.setDateUpdated(new Date().toString());
			userRepository.save(foundModel);
		} else {
			result = "Failed to log out with provided request...";
		}
		return result;
	}
	
	public void initMongoDB() {
		gson = new Gson();
		Page<JWTUser> users = userRepository.findAll(PageRequest.of(0, 10));
		if (!users.hasContent()) {
			// Insert users
			String strJWTUser = readFileAsObject("db/migration/JWT_USERS_V0.0.1.json");
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			List<JWTUser> migrateUsers = gson.fromJson(strJWTUser, new TypeToken<List<JWTUser>>(){}.getType());
			
			for (JWTUser user : migrateUsers) {
				user.setSalt(UUID.randomUUID().toString());
				String encodedPwd = encoder.encode(user.getPassword() + user.getSalt());
				LOGGER.error(encodedPwd);
				user.setPassword(encodedPwd);
			}
			userRepository.saveAll(migrateUsers);
		}
	}
	
	private String readFileAsObject(String worldFilePath) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(worldFilePath);
		InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(streamReader);
		StringBuilder strRead = new StringBuilder();
		try {
			for (String line; (line = reader.readLine()) != null;) {
				strRead.append(line);
			}
		} catch (IOException e) {
			LOGGER.error("Unable to read data from file: " + worldFilePath + " with exception: " + e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.error("To close input stream for file: " + worldFilePath + " with exception: " + e.getMessage());
			}
		}
		return strRead.toString();
	}
}
