package nwh.legendkeeper.gateway;

import org.junit.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@SpringBootConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
@ComponentScan({"nwh.legendkeeper.gateway", 
"nwh.legendkeeper.gateway.crypto", 
"nwh.legendkeeper.gateway.jwt"})
public class GatewayApplicationTests {
	
	public GatewayApplicationTests() {
		
	}

	@Test
	public void contextLoads() {
	}

}
