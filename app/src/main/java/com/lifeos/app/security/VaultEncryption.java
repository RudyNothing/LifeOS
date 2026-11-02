
package com.lifeos.app.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class VaultEncryption {

    private static final String KEY_ALIAS = "LifeOS_Vault_Key";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static SecretKey getSecretKey() throws Exception {

        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            return ((KeyStore.SecretKeyEntry)
                    keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
        );

        keyGenerator.init(new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setRandomizedEncryptionRequired(true)
                .build());

        return keyGenerator.generateKey();
    }

    public static String encrypt(String plainText) throws Exception {

        if (plainText == null || plainText.isEmpty()) {
            return "";
        }

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(
                plainText.getBytes(StandardCharsets.UTF_8)
        );

        byte[] combined = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length,
                encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedText) throws Exception {

        if (encryptedText == null || encryptedText.isEmpty()) {
            return "";
        }

        byte[] combined = Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[12];
        byte[] encrypted = new byte[combined.length - 12];

        System.arraycopy(combined, 0, iv, 0, 12);
        System.arraycopy(combined, 12, encrypted, 0,
                encrypted.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        cipher.init(
                Cipher.DECRYPT_MODE,
                getSecretKey(),
                new GCMParameterSpec(128, iv)
        );

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
