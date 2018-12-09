package projects.dinesh.housetaskmanager.utils

import android.content.Context

object AppPreferences {

    const val APP_LEVEL_PREFERENCE = "APP_LEVEL"

    fun isPreferenceEnabled(context: Context, key: String): Boolean {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        return sharedPreferences.getBoolean(key, false)
    }

    fun putBoolean(context: Context, key: String, value: Boolean) {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun putString(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putInteger(context: Context, key: String, value: Int?) {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value!!)
        editor.apply()
    }

    fun getString(context: Context, key: String, defaultValue: String): String? {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getBoolean(context: Context, key: String, defaultValue: Boolean?): Boolean {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        return sharedPreferences.getBoolean(key, defaultValue!!)
    }

    fun getInteger(context: Context, key: String, defaultValue: Int?): Int {
        val sharedPreferences = context.getSharedPreferences(APP_LEVEL_PREFERENCE, 0)
        return sharedPreferences.getInt(key, defaultValue!!)
    }
}
