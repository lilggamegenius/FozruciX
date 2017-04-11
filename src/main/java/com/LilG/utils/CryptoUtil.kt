package com.LilG.utils

import com.LilG.FozConfig
import com.LilG.FozConfig.Password
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec


/***
 * Encryption and Decryption of String data; PBE(Password Based Encryption and Decryption)

 * @author Vikram
 */
class CryptoUtil {
    companion object {
        private var ecipher: Cipher? = null
        private var dcipher: Cipher? = null
        // 8-byte Salt
        private var salt = FozConfig.setPassword(Password.salt).toByteArray()
        // Iteration count
        private val iterationCount = 19
        private val charSet = "UTF-8"

        @Throws(Exception::class)
        @JvmStatic fun main(args: Array<String>) {
            salt = FozConfig.setPassword(Password.salt).toByteArray()
            var plain: String
            var enc: String
            var plainAfter: String
            var decrypted: String
            for (pass in Password.values()) {
                try {
                    println(pass)
                    plain = FozConfig.setPassword(pass)
                    enc = CryptoUtil.encrypt(plain)
                    println("Original text: " + plain)
                    println("Encrypted text: " + enc)
                    plainAfter = CryptoUtil.decrypt(enc)
                    println("Original text after decryption: " + plainAfter)
                    decrypted = CryptoUtil.decrypt(plain)
                    println("Original text decrypted: " + decrypted)
                    println()
                } catch (ignored: Exception) {
                }

            }
        }

        fun encrypt(plainText: String): String {
            return encrypt(FozConfig.setPassword(Password.key), plainText)!!
        }

        /**
         * @param secretKey Key used to encrypt data
         * *
         * @param plainText Text input to be encrypted
         * *
         * @return Returns encrypted text
         */
        fun encrypt(secretKey: String, plainText: String): String? {
            try {
                //Key generation for enc and desc
                val keySpec = PBEKeySpec(secretKey.toCharArray(), salt, iterationCount)
                val key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec)
                // Prepare the parameter to the ciphers
                val paramSpec = PBEParameterSpec(salt, iterationCount)

                //Enc process
                ecipher = Cipher.getInstance(key.algorithm)
                ecipher!!.init(Cipher.ENCRYPT_MODE, key, paramSpec)
                val `in` = plainText.toByteArray(charset(charSet))
                val out = ecipher!!.doFinal(`in`)
                return sun.misc.BASE64Encoder().encode(out)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }

        fun decrypt(encryptedText: String): String {
            return decrypt(FozConfig.setPassword(FozConfig.Password.key), encryptedText)!!
        }

        /**
         * @param secretKey     Key used to decrypt data
         * *
         * @param encryptedText encrypted text input to decrypt
         * *
         * @return Returns plain text after decryption
         */
        fun decrypt(secretKey: String, encryptedText: String): String? {
            try {
                //Key generation for enc and desc
                val keySpec = PBEKeySpec(secretKey.toCharArray(), salt, iterationCount)
                val key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec)
                // Prepare the parameter to the ciphers
                val paramSpec = PBEParameterSpec(salt, iterationCount)
                //Decryption process; same key will be used for decr
                dcipher = Cipher.getInstance(key.algorithm)
                dcipher!!.init(Cipher.DECRYPT_MODE, key, paramSpec)
                val enc = sun.misc.BASE64Decoder().decodeBuffer(encryptedText)
                val utf8 = dcipher!!.doFinal(enc)
                return String(utf8)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }
}