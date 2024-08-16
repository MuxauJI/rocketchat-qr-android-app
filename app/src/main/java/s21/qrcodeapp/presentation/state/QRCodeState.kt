package s21.qrcodeapp.presentation.state

import android.graphics.Bitmap

sealed interface QRCodeState {
    data object TextGetQRCode : QRCodeState
    data class QRCode(val qrCode: Bitmap) : QRCodeState
    data object None : QRCodeState
}