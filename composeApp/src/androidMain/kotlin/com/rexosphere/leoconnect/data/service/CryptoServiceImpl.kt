package com.rexosphere.leoconnect.data.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.rexosphere.leoconnect.domain.service.CryptoService
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class CryptoServiceImpl(private val context: Context) : CryptoService {
    
    companion object {
        private const val KEY_ALIAS = "leo_connect_rsa_key"
        private const val PREFS_NAME = "crypto_prefs"
        private const val PUBLIC_KEY_PREF = "public_key_pem"
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val KEY_SIZE = 2048
    }

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun generateKeyPair(): Result<String> {
        return try {
            // Generate key pair in Android Keystore
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )

            val parameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setKeySize(KEY_SIZE)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            }.build()

            keyPairGenerator.initialize(parameterSpec)
            val keyPair = keyPairGenerator.generateKeyPair()

            // Convert public key to PEM format
            val publicKeyPem = publicKeyToPem(keyPair.public)

            // Store public key in encrypted preferences
            encryptedPrefs.edit().putString(PUBLIC_KEY_PREF, publicKeyPem).apply()

            Result.success(publicKeyPem)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to generate key pair: ${e.message}", e))
        }
    }

    override suspend fun getPublicKey(): String? {
        return encryptedPrefs.getString(PUBLIC_KEY_PREF, null)
    }

    override suspend fun getPrivateKey(): String? {
        // Private key is stored in Android Keystore and cannot be extracted
        // This method returns null as the key is hardware-backed
        return null
    }

    override suspend fun encrypt(plaintext: String, publicKeyPem: String): Result<String> {
        return try {
            val publicKey = pemToPublicKey(publicKeyPem)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

            Result.success(encryptedBase64)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to encrypt message: ${e.message}", e))
        }
    }

    override suspend fun decrypt(ciphertext: String): Result<String> {
        return try {
            val privateKey = getPrivateKeyFromKeystore()
                ?: return Result.failure(Exception("Private key not found. Please generate a key pair first."))

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            val encryptedBytes = Base64.decode(ciphertext, Base64.NO_WRAP)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            val decryptedText = String(decryptedBytes, Charsets.UTF_8)

            Result.success(decryptedText)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to decrypt message: ${e.message}", e))
        }
    }

    override suspend fun hasKeyPair(): Boolean {
        return keyStore.containsAlias(KEY_ALIAS) && getPublicKey() != null
    }

    override suspend fun clearKeys() {
        try {
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
            encryptedPrefs.edit().remove(PUBLIC_KEY_PREF).apply()
        } catch (e: Exception) {
            // Log error but don't throw
            println("Error clearing keys: ${e.message}")
        }
    }

    private fun getPrivateKeyFromKeystore(): PrivateKey? {
        return try {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
            entry?.privateKey
        } catch (e: Exception) {
            null
        }
    }

    private fun publicKeyToPem(publicKey: PublicKey): String {
        val encoded = Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
        return "-----BEGIN PUBLIC KEY-----\n$encoded\n-----END PUBLIC KEY-----"
    }

    private fun pemToPublicKey(pem: String): PublicKey {
        val publicKeyPEM = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val decoded = Base64.decode(publicKeyPEM, Base64.NO_WRAP)
        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        val keySpec = java.security.spec.X509EncodedKeySpec(decoded)
        return keyFactory.generatePublic(keySpec)
    }
}
