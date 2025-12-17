package com.rexosphere.leoconnect.domain.service

/**
 * Service for cryptographic operations (RSA encryption/decryption)
 * Platform-specific implementations handle key generation and storage
 */
interface CryptoService {
    /**
     * Generate a new RSA key pair and store it securely
     * @return Result containing the public key in PEM format, or error
     */
    suspend fun generateKeyPair(): Result<String>

    /**
     * Get the stored public key in PEM format
     * @return Public key string or null if not generated yet
     */
    fun getPublicKey(): String?

    /**
     * Get the stored private key (for internal use only)
     * @return Private key or null if not generated yet
     */
    fun getPrivateKey(): String?

    /**
     * Encrypt a message using the recipient's public key
     * @param plaintext The message to encrypt
     * @param publicKeyPem The recipient's public key in PEM format
     * @return Result containing encrypted message (Base64 encoded) or error
     */
    suspend fun encrypt(plaintext: String, publicKeyPem: String): Result<String>

    /**
     * Decrypt a message using the stored private key
     * @param ciphertext The encrypted message (Base64 encoded)
     * @return Result containing decrypted plaintext or error
     */
    suspend fun decrypt(ciphertext: String): Result<String>

    /**
     * Check if a key pair exists
     */
    fun hasKeyPair(): Boolean

    /**
     * Clear all stored keys
     */
    fun clearKeys()
}
