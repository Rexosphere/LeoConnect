package com.rexosphere.leoconnect.data.source.local

import java.util.prefs.Preferences

class JvmPreferences : LeoPreferences {
    private val prefs = Preferences.userRoot().node("leo_connect")

    override fun putString(key: String, value: String) {
        prefs.put(key, value)
        prefs.flush()
    }

    override fun getString(key: String): String? {
        return prefs.get(key, null)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
        prefs.flush()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun remove(key: String) {
        prefs.remove(key)
        prefs.flush()
    }

    override fun clear() {
        prefs.clear()
        prefs.flush()
    }
}
