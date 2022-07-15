package com.teco.apparchitecture

import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.io.IOException
import java.security.*
import java.util.*

class Demo {
    object RSAEncryptDecrypt {

        var privateKey: PrivateKey
        var publicKey: PublicKey

        private val keyFactory = KeyFactory.getInstance("RSA");
        private val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

        private fun loadPublicKey(stored: String): Key? {
            return keyFactory.generatePublic(
                X509EncodedKeySpec(
                    Base64.getDecoder().decode(stored.toByteArray())
                )
            )
        }

        private fun loadPrivateKey(key64: String): Key? {
            val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val priv = keyFactory.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }

        fun encryptMessage(plainText: String, publicKey: String): String{
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publicKey))
            return Base64.getEncoder().encodeToString(cipher.doFinal
                (plainText.toByteArray()))
        }

        fun decryptMessage (encryptedText: String, privateKey: String): String {
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKey))
            return String(cipher.doFinal(Base64.getDecoder().
            decode(encryptedText)))
        }

        init {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(1024)
            val pair = keyGen.generateKeyPair()
            privateKey = pair.private
            publicKey = pair.public
        }

    }
}