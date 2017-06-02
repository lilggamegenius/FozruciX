using System;
using ikvm.extensions;
using java.security.spec;
using javax.crypto;
using javax.crypto.spec;

namespace FozruciCS.Utils{
	public static class CryptoUtil{
		private static Cipher _ecipher;

		private static Cipher _dcipher;

		// 8-byte Salt
		private static byte[] _salt = FozConfig.setPassword(Password.Salt).getBytes();

		// Iteration count
		private static int iterationCount = 19;

		private static string charSet = "UTF-8";

		public static void main(string[] args){
			_salt = FozConfig.setPassword(Password.Salt).getBytes();
			string plain, enc, plainAfter, decrypted;
			foreach(Password pass in Enum.GetValues(typeof(Password))){
				try{
					System.Console.WriteLine(pass);
					plain = FozConfig.setPassword(pass);
					enc = encrypt(plain);
					System.Console.WriteLine("Original text: " + plain);
					System.Console.WriteLine("Encrypted text: " + enc);
					plainAfter = decrypt(enc);
					System.Console.WriteLine("Original text after decryption: " + plainAfter);
					decrypted = decrypt(plain);
					System.Console.WriteLine("Original text decrypted: " + decrypted);
					System.Console.WriteLine();
				}
				catch(Exception){ // ignored
 }
			}
		}

		public static string encrypt(string plainText){
			return encrypt(FozConfig.setPassword(Password.Key), plainText);
		}

/**
 * @param secretKey Key used to encrypt data
 * @param plainText Text input to be encrypted
 * @return Returns encrypted text
 */
		public static string encrypt(string secretKey, string plainText){
			try{
//Key generation for enc and desc
				KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), _salt, iterationCount);
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
// Prepare the parameter to the ciphers
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(_salt, iterationCount);

//Enc process
				_ecipher = Cipher.getInstance(key.getAlgorithm());
				_ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
				byte[] in_ = plainText.getBytes(charSet);
				byte[] out_ = _ecipher.doFinal(in_);
				return new sun.misc.BASE64Encoder().encode(out_);
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}

		public static string decrypt(string encryptedText){
			return decrypt(FozConfig.setPassword(Password.Key), encryptedText);
		}

/**
 * @param secretKey     Key used to decrypt data
 * @param encryptedText encrypted text input to decrypt
 * @return Returns plain text after decryption
 */
		public static string decrypt(string secretKey, string encryptedText){
			try{
//Key generation for enc and desc
				KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), _salt, iterationCount);
				SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
// Prepare the parameter to the ciphers
				AlgorithmParameterSpec paramSpec = new PBEParameterSpec(_salt, iterationCount);
//Decryption process; same key will be used for decr
				_dcipher = Cipher.getInstance(key.getAlgorithm());
				_dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
				byte[] enc = new sun.misc.BASE64Decoder().decodeBuffer(encryptedText);
				byte[] utf8 = _dcipher.doFinal(enc);
				return new string(System.Text.Encoding.UTF8.GetString(utf8).ToCharArray());
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
}
