package com.rexosphere.leoconnect.data.source.local

import platform.Foundation.NSUserDefaults

class IosPreferences : LeoPreferences {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override fun getString(key: String): String? {
        return userDefaults.stringForKey(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        userDefaults.setBool(value, forKey = key)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (userDefaults.objectForKey(key) != null) {
            userDefaults.boolForKey(key)
        } else {
            defaultValue
        }
    }

    override fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override fun clear() {
        val domain = NSUserDefaults.standardUserDefaults.dictionaryRepresentation().keys
        domain.forEach { key ->
            if (key is String) {
                userDefaults.removeObjectForKey(key)
            }
        }
    }
}
