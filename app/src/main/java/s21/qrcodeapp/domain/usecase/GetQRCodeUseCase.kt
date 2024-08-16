package s21.qrcodeapp.domain.usecase

import s21.qrcodeapp.domain.model.QRCode
import s21.qrcodeapp.domain.repository.QRCodeRepository

class GetQRCodeUseCase(private val repository: QRCodeRepository) {

    suspend operator fun invoke(): QRCode {
        return repository.getQRCode()
    }
}