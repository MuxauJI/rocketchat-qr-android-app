package s21.qrcodeapp.data.datasource.preferences

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var token: String?
        get() = sharedPreferences.getString(TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(TOKEN, value).apply()
        }

    companion object {
        const val PREFERENCES_NAME = "QRCodeAppPreferences"
        const val TOKEN = "token"
    }
}