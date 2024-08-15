package s21.qrcodeapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import s21.qrcodeapp.ui.theme.QRCodeAppTheme
import java.io.ByteArrayInputStream


class MainActivity : ComponentActivity() {
    private lateinit var preferencesManager: PreferencesManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preferencesManager = PreferencesManager(this)
        setContent {
            QRCodeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QRCodeApp(
                        modifier = Modifier.padding(innerPadding),
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}

@Composable
fun QRCodeApp(modifier: Modifier = Modifier, preferencesManager: PreferencesManager) {
    var token by remember { mutableStateOf(preferencesManager.token ?: "") }
    var qrCodeImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val botUserName by remember { mutableStateOf("qr-code-generator.bot") }
    val webSockerUri by remember { mutableStateOf("wss://rocketchat-student.21-school.ru/websocket") }

    // Сохранение токена в SharedPreferences при его изменении
    LaunchedEffect(token) {
        preferencesManager.token = token
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Input field
        OutlinedTextField(
            value = token,
            onValueChange = { newToken ->
                token = newToken
            },
            label = { Text("Enter Token") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Используем корутины для выполнения сетевой операции
            CoroutineScope(Dispatchers.IO).launch {
                val client = RocketChatWebSocketClient(webSockerUri, token, botUserName)
                client.connect()

                synchronized(client) {
                    while (!client.finished) {
                        client.wait()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (client.responseString != null) {
                        val base64String = client.responseString?.removePrefix("data:image/png;base64,")
                        qrCodeImage = base64ToBitmap(base64String.orEmpty())
                        if (qrCodeImage == null) {
                            errorMessage = "Failed to decode image."
                        }
                    } else {
                        errorMessage = "Failed to retrieve QR code."
                    }
                }
            }
        }) {
            Text("Update QR Code")
        }

        Spacer(modifier = Modifier.height(16.dp))

        qrCodeImage?.let { image ->
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier
                    .size(400.dp)
                    .background(Color.Gray)
            )
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }
    }
}

fun base64ToBitmap(base64String: String): ImageBitmap? {
    return try {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        val inputStream = ByteArrayInputStream(decodedString)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
