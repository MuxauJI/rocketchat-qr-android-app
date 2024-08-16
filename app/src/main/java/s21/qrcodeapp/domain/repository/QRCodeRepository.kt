package s21.qrcodeapp.domain.repository

import s21.qrcodeapp.domain.model.QRCode

interface QRCodeRepository {
    suspend fun getQRCode(): QRCode
}