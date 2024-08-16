package s21.qrcodeapp.presentation.state

sealed interface TokenState {
    data class ChangeToken(val token: String = "") : TokenState
    data class SettedToken(val token: String) : TokenState
}