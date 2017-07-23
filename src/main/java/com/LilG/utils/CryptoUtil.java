package com.LilG.utils;

import com.LilG.FozConfig;
import com.LilG.FozConfig.Password;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;


/***
 * Encryption and Decryption of String data; PBE(Password Based Encryption and Decryption)
 *
 * @author Vikram
 */
public class CryptoUtil {
	private static Cipher ecipher;
	private static Cipher dcipher;
	// 8-byte Salt
	private static byte[] salt = FozConfig.setPassword(Password.salt).getBytes();
	// Iteration count
	private static int iterationCount = 19;
	private static String charSet = "UTF-8";

	public CryptoUtil() {
	}

	public static void main(String[] args) throws Exception {
		salt = FozConfig.setPassword(Password.salt).getBytes();
		String plain, enc, plainAfter, decrypted;
		for (Password pass : Password.values()) {
			try {
				System.out.println(pass);
				plain = FozConfig.setPassword(pass);
				enc = CryptoUtil.encrypt(plain);
				System.out.println("Original text: " + plain);
				System.out.println("Encrypted text: " + enc);
				plainAfter = CryptoUtil.decrypt(enc);
				System.out.println("Original text after decryption: " + plainAfter);
				decrypted = CryptoUtil.decrypt(plain);
				System.out.println("Original text decrypted: " + decrypted);
				System.out.println();
			} catch (Exception ignored) {
			}
		}
	}

	public static String encrypt(String plainText) {
		return encrypt(FozConfig.setPassword(Password.key), plainText);
	}

	/**
	 * @param secretKey Key used to encrypt data
	 * @param plainText Text input to be encrypted
	 * @return Returns encrypted text
	 */
	public static String encrypt(String secretKey, String plainText) {
		try {
			//Key generation for enc and desc
			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			//Enc process
			ecipher = Cipher.getInstance(key.getAlgorithm());
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			byte[] in = plainText.getBytes(charSet);
			byte[] out = ecipher.doFinal(in);
			return new sun.misc.BASE64Encoder().encode(out);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String encryptedText) {
		return decrypt(FozConfig.setPassword(FozConfig.Password.key), encryptedText);
	}

	/**
	 * @param secretKey     Key used to decrypt data
	 * @param encryptedText encrypted text input to decrypt
	 * @return Returns plain text after decryption
	 */
	public static String decrypt(String secretKey, String encryptedText) {
		try {
			//Key generation for enc and desc
			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
			//Decryption process; same key will be used for decr
			dcipher = Cipher.getInstance(key.getAlgorithm());
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			byte[] enc = new sun.misc.BASE64Decoder().decodeBuffer(encryptedText);
			byte[] utf8 = dcipher.doFinal(enc);
			return new String(utf8, charSet);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}