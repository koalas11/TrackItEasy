package com.kotlinenjoyers.trackiteasy.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoManager {
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    /**
     * Chosen AES key size in bits.
     */
    private const val AES_KEY_SIZE = 256

    /**
     * Android KeyStore type.
     */
    private const val ANDROID_KEY_STORE = "AndroidKeyStore"

    /**
     * Alias for the application AES key.
     */
    private const val ALIAS_KEY = "secret"

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    private val encryptCipher: Cipher get() = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, getKey()) }

    private fun getDecryptCipherForIv(gcmParameterSpec: GCMParameterSpec): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), gcmParameterSpec)
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS_KEY, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    ALIAS_KEY,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                .setKeySize(AES_KEY_SIZE)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: DataOutputStream): ByteArray {
        val cipher = encryptCipher

        val ciphertext = cipher.doFinal(bytes)
        outputStream.use {
            it.write(cipher.iv.size)
            it.write(cipher.iv)
            it.write(ciphertext.size - bytes.size)
            it.writeInt(ciphertext.size)
            it.write(ciphertext)
        }
        return ciphertext
    }

    fun decrypt(inputStream: DataInputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val tagSize = it.read()
            val encryptedSize = it.readInt()
            val encryptedBytes = ByteArray(encryptedSize)
            it.read(encryptedBytes)

            val cipher = getDecryptCipherForIv(GCMParameterSpec(tagSize * 8, iv))
            val plaintext = cipher.doFinal(encryptedBytes)
            plaintext
        }
    }
}