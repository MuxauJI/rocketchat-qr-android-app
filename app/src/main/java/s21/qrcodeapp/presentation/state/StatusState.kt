package s21.qrcodeapp.presentation.state

sealed interface StatusState {
    object None : StatusState
    object Loading : StatusState
    object TokenSaved : StatusState
    object ValidQR : StatusState
    data class Error(val error: String) : StatusState
}