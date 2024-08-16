package s21.qrcodeapp.domain.model

import s21.qrcodeapp.data.repository.QRCodeError

sealed interface QRCode {
    data class Bitmap(val bitmap: android.graphics.Bitmap) : QRCode
    data class Error(val error: QRCodeError) : QRCode {
        override fun toString(): String = error.toString()
    }
}