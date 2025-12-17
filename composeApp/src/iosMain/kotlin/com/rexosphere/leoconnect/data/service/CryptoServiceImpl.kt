package com.rexosphere.leoconnect.data.service

import com.rexosphere.leoconnect.domain.service.CryptoService
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class CryptoServiceImpl : CryptoService {
    
    companion object {
        private const val KEY_TAG = "com.rexosphere.leoconnect.rsaKey"
        private const val PUBLIC_KEY_PREF = "public_key_pem"
        private const val KEY_SIZE = 2048
    }

    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun generateKeyPair(): Result<String> {
        return try {
            // Create key generation parameters
            val parameters = mapOf<Any?, Any?>(
                kSecAttrKeyType to kSecAttrKeyTypeRSA,
                kSecAttrKeySizeInBits to KEY_SIZE,
                kSecPrivateKeyAttrs to mapOf<Any?, Any?>(
                    kSecAttrIsPermanent to true,
                    kSecAttrApplicationTag to KEY_TAG.toNSData()
                )
            )

            memScoped {
                val error = alloc<ObjCObjectVar<CFErrorRef?>>()
                val privateKey = SecKeyCreateRandomKey(parameters as CFDictionaryRef, error.ptr)

                if (privateKey == null) {
                    val errorDesc = error.value?.let { CFErrorCopyDescription(it) as String } ?: "Unknown error"
                    return Result.failure(Exception("Failed to generate key pair: $errorDesc"))
                }

                // Get public key from private key
                val publicKey = SecKeyCopyPublicKey(privateKey)
                    ?: return Result.failure(Exception("Failed to extract public key"))

                // Convert public key to PEM format
                val publicKeyData = SecKeyCopyExternalRepresentation(publicKey, error.ptr)
                    ?: return Result.failure(Exception("Failed to export public key"))

                val publicKeyPem = publicKeyToPem(publicKeyData as NSData)

                // Store public key in UserDefaults
                userDefaults.setObject(publicKeyPem, PUBLIC_KEY_PREF)
                userDefaults.synchronize()

                Result.success(publicKeyPem)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to generate key pair: ${e.message}", e))
        }
    }

    override fun getPublicKey(): String? {
        return userDefaults.stringForKey(PUBLIC_KEY_PREF)
    }

    override fun getPrivateKey(): String? {
        // Private key is stored in iOS Keychain and cannot be extracted
        return null
    }

    override suspend fun encrypt(plaintext: String, publicKeyPem: String): Result<String> {
        return try {
            val publicKey = pemToPublicKey(publicKeyPem)
                ?: return Result.failure(Exception("Invalid public key format"))

            val plainData = plaintext.toNSData()

            memScoped {
                val error = alloc<ObjCObjectVar<CFErrorRef?>>()
                val encryptedData = SecKeyCreateEncryptedData(
                    publicKey,
                    kSecKeyAlgorithmRSAEncryptionPKCS1,
                    plainData as CFDataRef,
                    error.ptr
                )

                if (encryptedData == null) {
                    val errorDesc = error.value?.let { CFErrorCopyDescription(it) as String } ?: "Unknown error"
                    return Result.failure(Exception("Encryption failed: $errorDesc"))
                }

                val base64 = (encryptedData as NSData).base64EncodedStringWithOptions(0u)
                Result.success(base64)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to encrypt message: ${e.message}", e))
        }
    }

    override suspend fun decrypt(ciphertext: String): Result<String> {
        return try {
            val privateKey = getPrivateKeyFromKeychain()
                ?: return Result.failure(Exception("Private key not found. Please generate a key pair first."))

            val encryptedData = NSData.create(base64EncodedString = ciphertext, options = 0u)
                ?: return Result.failure(Exception("Invalid ciphertext format"))

            memScoped {
                val error = alloc<ObjCObjectVar<CFErrorRef?>>()
                val decryptedData = SecKeyCreateDecryptedData(
                    privateKey,
                    kSecKeyAlgorithmRSAEncryptionPKCS1,
                    encryptedData as CFDataRef,
                    error.ptr
                )

                if (decryptedData == null) {
                    val errorDesc = error.value?.let { CFErrorCopyDescription(it) as String } ?: "Unknown error"
                    return Result.failure(Exception("Decryption failed: $errorDesc"))
                }

                val decryptedText = (decryptedData as NSData).toKString()
                Result.success(decryptedText)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to decrypt message: ${e.message}", e))
        }
    }

    override fun hasKeyPair(): Boolean {
        return getPrivateKeyFromKeychain() != null && getPublicKey() != null
    }

    override fun clearKeys() {
        try {
            // Delete private key from keychain
            val query = mapOf<Any?, Any?>(
                kSecClass to kSecClassKey,
                kSecAttrApplicationTag to KEY_TAG.toNSData(),
                kSecAttrKeyType to kSecAttrKeyTypeRSA
            )
            SecItemDelete(query as CFDictionaryRef)

            // Remove public key from UserDefaults
            userDefaults.removeObjectForKey(PUBLIC_KEY_PREF)
            userDefaults.synchronize()
        } catch (e: Exception) {
            println("Error clearing keys: ${e.message}")
        }
    }

    private fun getPrivateKeyFromKeychain(): SecKeyRef? {
        val query = mapOf<Any?, Any?>(
            kSecClass to kSecClassKey,
            kSecAttrApplicationTag to KEY_TAG.toNSData(),
            kSecAttrKeyType to kSecAttrKeyTypeRSA,
            kSecReturnRef to true
        )

        memScoped {
            val result = alloc<ObjCObjectVar<CFTypeRef?>>()
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)

            return if (status == errSecSuccess) {
                result.value as? SecKeyRef
            } else {
                null
            }
        }
    }

    private fun publicKeyToPem(keyData: NSData): String {
        val base64 = keyData.base64EncodedStringWithOptions(NSDataBase64Encoding64CharacterLineLength)
        return "-----BEGIN PUBLIC KEY-----\n$base64\n-----END PUBLIC KEY-----"
    }

    private fun pemToPublicKey(pem: String): SecKeyRef? {
        val publicKeyPEM = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyData = NSData.create(base64EncodedString = publicKeyPEM, options = 0u)
            ?: return null

        val attributes = mapOf<Any?, Any?>(
            kSecAttrKeyType to kSecAttrKeyTypeRSA,
            kSecAttrKeyClass to kSecAttrKeyClassPublic,
            kSecAttrKeySizeInBits to KEY_SIZE
        )

        memScoped {
            val error = alloc<ObjCObjectVar<CFErrorRef?>>()
            return SecKeyCreateWithData(keyData as CFDataRef, attributes as CFDictionaryRef, error.ptr)
        }
    }

    // Extension functions for conversions
    private fun String.toNSData(): NSData {
        return this.encodeToByteArray().toNSData()
    }

    private fun ByteArray.toNSData(): NSData {
        return NSData.create(bytes = this.refTo(0).getPointer(MemScope()), length = this.size.toULong())
    }

    private fun NSData.toKString(): String {
        return this.bytes?.readBytes(this.length.toInt())?.decodeToString() ?: ""
    }
}
