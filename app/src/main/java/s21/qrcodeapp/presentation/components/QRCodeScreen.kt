package s21.qrcodeapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import s21.qrcodeapp.R
import s21.qrcodeapp.presentation.state.QRCodeState
import s21.qrcodeapp.presentation.QRCodeViewModel
import s21.qrcodeapp.presentation.state.StatusState

@Composable
fun QRCodeScreen(viewModel: QRCodeViewModel = hiltViewModel()) {
    val qrState by viewModel.qrCodeState.collectAsState()
    val statusState by viewModel.statusState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                if (statusState !is StatusState.ValidQR) viewModel.getQRCode()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = qrState) {
            is QRCodeState.None -> {}

            is QRCodeState.QRCode -> {
                Image(
                    bitmap = state.qrCode.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp)
                )
            }

            QRCodeState.TextGetQRCode -> {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(R.string.click_to_generate_qr),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
