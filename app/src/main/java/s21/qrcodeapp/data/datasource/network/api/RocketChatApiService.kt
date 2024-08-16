package s21.qrcodeapp.data.datasource.network.api

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import s21.qrcodeapp.data.datasource.network.websocket.RocketChatWebSocketClient


interface RocketChatApiService {
    suspend fun retrieveQrCode(token: String): String?
}

class RocketChatApiServiceImpl(
    private val webSocketUri: String
) : RocketChatApiService {
    override suspend fun retrieveQrCode(token: String): String? = withContext(Dispatchers.IO) {
        val deferredResponse = CompletableDeferred<String?>()
        val client = RocketChatWebSocketClient(webSocketUri, token).apply {
            responseHandler = { response ->
                Log.d("RocketChatApiServiceImpl", "Response received: $response")
                deferredResponse.complete(response)
            }
            errorHandler = { error ->
                Log.e("RocketChatApiServiceImpl", "Error received: $error")
                deferredResponse.complete(null)
            }
        }

        try {
            client.connect()
            return@withContext deferredResponse.await()
        } catch (e: Exception) {
            Log.e("RocketChatApiServiceImpl", "Exception: ${e.message}")
            return@withContext null
        }
    }
}