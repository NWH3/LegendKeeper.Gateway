package nwh.legendkeeper.gateway;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import nwh.legendkeeper.gateway.crypto.RSACrypto;
import nwh.legendkeeper.gateway.crypto.RSAKeyGenerator;

@EnableZuulProxy
@SpringBootApplication
@ComponentScan({"nwh.legendkeeper.gateway", 
"nwh.legendkeeper.gateway.crypto", 
"nwh.legendkeeper.gateway.jwt"})
public class GatewayApplication {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
		// Generate public and private RSA keys 
		RSAKeyGenerator gk = new RSAKeyGenerator();
		gk.generateKeyPair();
		gk.writeToFile(RSACrypto.PUBLIC_KEY_FILE, gk.getPublicKey().getEncoded());
		gk.writeToFile(RSACrypto.PRIVATE_KEY_FILE, gk.getPrivateKey().getEncoded());
		
		ConfigurableApplicationContext context = SpringApplication.run(GatewayApplication.class, args);
		context.getBean(GatewayService.class).initMongoDB();
	}

}
