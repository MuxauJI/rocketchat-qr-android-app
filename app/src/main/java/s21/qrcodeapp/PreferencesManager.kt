package s21.qrcodeapp

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("QRCodeAppPreferences", Context.MODE_PRIVATE)

    var token: String?
        get() = sharedPreferences.getString("token", null)
        set(value) {
            sharedPreferences.edit().putString("token", value).apply()
        }
}

