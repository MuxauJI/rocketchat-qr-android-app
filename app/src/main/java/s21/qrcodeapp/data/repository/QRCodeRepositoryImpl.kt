package s21.qrcodeapp.data.repository

import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import s21.qrcodeapp.data.datasource.network.api.RocketChatApiService
import s21.qrcodeapp.data.datasource.preferences.PreferencesDataSource
import s21.qrcodeapp.domain.model.QRCode
import s21.qrcodeapp.domain.repository.QRCodeRepository
import java.io.ByteArrayInputStream
import javax.inject.Inject

class QRCodeRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val apiService: RocketChatApiService
) : QRCodeRepository {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getQRCode(): QRCode {
        return try {
            val token = preferencesDataSource.token ?: return QRCode.Error(QRCodeError.NoTokenFound)
            val qrCode = coroutineScope.async {
                apiService.retrieveQrCode(token)
            }
            val base64Image = qrCode.await() ?: return QRCode.Error(QRCodeError.NoQRCodeFound)
            val decodedString =
                Base64.decode(base64Image.removePrefix("data:image/png;base64,"), Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(decodedString)
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: return QRCode.Error(QRCodeError.FailedToDecodeImage)
            QRCode.Bitmap(bitmap)
        } catch (e: Exception) {
            QRCode.Error(QRCodeError.UnknownError)
        }
    }
}

sealed class QRCodeError(private val message: String) {
    object NoTokenFound : QRCodeError("No token found")
    object NoQRCodeFound : QRCodeError("No QR code found")
    object UnknownError : QRCodeError("Unknown error")
    object FailedToDecodeImage : QRCodeError("Failed to decode image")

    override fun toString(): String = message
}
