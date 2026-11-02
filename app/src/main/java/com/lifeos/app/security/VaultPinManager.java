
package com.lifeos.app.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class VaultPinManager {

    private static final String PREF_NAME = "lifeos_vault_security";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_PIN_SALT = "pin_salt";

    private final SharedPreferences preferences;

    public VaultPinManager(Context context) {
        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public boolean isPinSet() {
        return preferences.contains(KEY_PIN_HASH)
                && preferences.contains(KEY_PIN_SALT);
    }

    public boolean setPin(String pin) {

        if (pin == null || !pin.matches("\\d{4,6}")) {
            return false;
        }

        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            String saltString = Base64.encodeToString(
                    salt, Base64.NO_WRAP
            );

            String hash = hashPin(pin, salt);

            preferences.edit()
                    .putString(KEY_PIN_SALT, saltString)
                    .putString(KEY_PIN_HASH, hash)
                    .apply();

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyPin(String pin) {

        if (pin == null || !isPinSet()) {
            return false;
        }

        try {
            String saltString = preferences.getString(
                    KEY_PIN_SALT, null
            );

            String storedHash = preferences.getString(
                    KEY_PIN_HASH, null
            );

            byte[] salt = Base64.decode(
                    saltString, Base64.NO_WRAP
            );

            String enteredHash = hashPin(pin, salt);

            return MessageDigest.isEqual(
                    storedHash.getBytes(),
                    enteredHash.getBytes()
            );

        } catch (Exception e) {
            return false;
        }
    }

    private String hashPin(String pin, byte[] salt)
            throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);

        byte[] hash = digest.digest(pin.getBytes("UTF-8"));

        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }
}
