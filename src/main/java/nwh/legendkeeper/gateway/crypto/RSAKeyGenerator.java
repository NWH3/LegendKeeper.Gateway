package nwh.legendkeeper.gateway.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Class used to generate public & private key pairings for use of payload
 * encryption when authenticating
 * 
 * @author Nathanial.Heard
 *
 */
public class RSAKeyGenerator {

	private static final int KEY_SIZE = 1024;

	private KeyPairGenerator keyGen;

	private KeyPair keyPair;

	private PrivateKey privateKey;

	private PublicKey publicKey;

	public RSAKeyGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
		this.keyGen = KeyPairGenerator.getInstance(RSACrypto.RSA);
		this.keyGen.initialize(KEY_SIZE);
	}

	public void generateKeyPair() {
		this.keyPair = this.keyGen.generateKeyPair();
		this.privateKey = keyPair.getPrivate();
		this.publicKey = keyPair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public void writeToFile(String filePath, byte[] key) throws IOException {
		File file = new File(filePath);
		file.getParentFile().mkdirs();

		FileOutputStream filtOut = new FileOutputStream(file);
		filtOut.write(key);
		filtOut.flush();
		filtOut.close();
	}
}