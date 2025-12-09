package com.rexosphere.leoconnect.data.source.local

import android.content.Context
import android.content.SharedPreferences

class AndroidPreferences(context: Context) : LeoPreferences {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "leo_connect_prefs",
        Context.MODE_PRIVATE
    )

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}
