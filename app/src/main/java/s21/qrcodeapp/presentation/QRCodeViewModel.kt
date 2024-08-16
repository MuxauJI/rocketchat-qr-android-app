package s21.qrcodeapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import s21.qrcodeapp.data.datasource.preferences.PreferencesDataSource
import s21.qrcodeapp.domain.model.QRCode
import s21.qrcodeapp.domain.usecase.GetQRCodeUseCase
import s21.qrcodeapp.presentation.state.QRCodeState
import s21.qrcodeapp.presentation.state.StatusState
import s21.qrcodeapp.presentation.state.TokenState
import javax.inject.Inject

@HiltViewModel
class QRCodeViewModel @Inject constructor(
    private val getQRCodeUseCase: GetQRCodeUseCase,
    private val preferencesDataSource: PreferencesDataSource
) : ViewModel() {

    private val _qrCodeState: MutableStateFlow<QRCodeState> =
        MutableStateFlow(QRCodeState.TextGetQRCode)
    val qrCodeState: StateFlow<QRCodeState> = _qrCodeState

    private val _tokenState: MutableStateFlow<TokenState> =
        MutableStateFlow(TokenState.ChangeToken())
    val tokenState: StateFlow<TokenState> = _tokenState

    private val _statusState: MutableStateFlow<StatusState> =
        MutableStateFlow(StatusState.None)
    val statusState: StateFlow<StatusState> = _statusState

    private var qrCodeTimestamp: Long? = null

    private var token: String
        get() = preferencesDataSource.token ?: ""
        set(value) {
            preferencesDataSource.token = value
        }

    init {
        initStateQRCode()
    }

    private fun initStateQRCode() {
        if (token.isEmpty()) {
            _tokenState.value = TokenState.ChangeToken(token)
            Log.d("QRCodeViewModel", "TokenState.ChangeToken")
        } else {
            _tokenState.value = TokenState.SettedToken(token)
            Log.d("QRCodeViewModel", "TokenState.SettedToken")
        }
    }

    fun saveToken(newToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            token = newToken
            _tokenState.value = TokenState.SettedToken(token)
            Log.d("QRCodeViewModel", "TokenState.SettedToken")
            _statusState.value = StatusState.TokenSaved
            Log.d("QRCodeViewModel", "StatusState.TokenSaved")
        }
    }

    fun changeToken() {
        _tokenState.value = TokenState.ChangeToken(token)
        Log.d("QRCodeViewModel", "TokenState.ChangeToken")
    }

    fun getQRCode() {
        _qrCodeState.value = QRCodeState.None
        if (token.isEmpty()) {
            _statusState.value = StatusState.Error("Token is not set")
            Log.d("QRCodeViewModel", "StatusState.Error")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _statusState.value = StatusState.Loading
            Log.d("QRCodeViewModel", "StatusState.Loading")
            when (val qrCode = getQRCodeUseCase()) {
                is QRCode.Bitmap -> {
                    qrCodeTimestamp = System.currentTimeMillis()
                    _qrCodeState.value = QRCodeState.QRCode(qrCode.bitmap)
                    Log.d("QRCodeViewModel", "QRCodeState.QRCode")
                    _statusState.value = StatusState.None
                    Log.d("QRCodeViewModel", "StatusState.None")

                    viewModelScope.launch(Dispatchers.IO) {
                        _statusState.value = StatusState.ValidQR
                        Log.d("QRCodeViewModel", "StatusState.ValidQR")
                        delay(30_000)
                        checkQRCodeValidity()
                    }
                    Log.d("QRCodeViewModel", "QRCodeState.None")
                }
                is QRCode.Error -> {
                    _qrCodeState.value = QRCodeState.None
                    Log.d("QRCodeViewModel", "QRCodeState.None")
                    _statusState.value = StatusState.Error(qrCode.error.toString())
                    Log.d("QRCodeViewModel", "StatusState.Error")
                }
            }
        }
    }

    private fun checkQRCodeValidity() {
        val currentTime = System.currentTimeMillis()
        val qrCodeValid = qrCodeTimestamp?.let { currentTime - it <= 30_000 } ?: false
        if (!qrCodeValid) {
            _statusState.value = StatusState.Error("QR Code expired")
            Log.d("QRCodeViewModel", "StatusState.Error")
        }
    }
}
