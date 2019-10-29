package nwh.legendkeeper.gateway.crypto;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.util.Base64;

/**
 * Class used to encrypt and decrypt 
 * using RSA asymmetric encryption Algorithm
 * 
 * @author Nathanial.Heard
 *
 */
public class RSACrypto {

	private Cipher cipher;

	public static final String PRIVATE_KEY_FILE = "keyPair/privateKey";
	public static final String PUBLIC_KEY_FILE = "keyPair/publicKey";
	public static final String RSA = "RSA";

	public RSACrypto() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.cipher = Cipher.getInstance(RSA);
	}

	public PrivateKey getPrivateKey()
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(new File(PRIVATE_KEY_FILE).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		return kf.generatePrivate(spec);
	}

	public PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(new File(PUBLIC_KEY_FILE).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(RSA);
		return kf.generatePublic(spec);
	}

	public String encrypt(String message, PublicKey key) throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		return new String(Base64.getEncoder().encode(cipher.doFinal(message.getBytes("UTF-8"))));
	}

	public String decrypt(String message, PrivateKey key)
			throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		return new String(cipher.doFinal(Base64.getDecoder().decode(message)), "UTF-8");
	}
}
