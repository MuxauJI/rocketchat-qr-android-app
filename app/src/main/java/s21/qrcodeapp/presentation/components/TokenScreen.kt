package s21.qrcodeapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import s21.qrcodeapp.R
import s21.qrcodeapp.presentation.QRCodeViewModel
import s21.qrcodeapp.presentation.state.TokenState

@Composable
fun TokenScreen(viewModel: QRCodeViewModel = hiltViewModel(), tokenSaved: () -> Unit) {
    val tokenState by viewModel.tokenState.collectAsState()
    var inputToken by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (tokenState) {
            is TokenState.ChangeToken -> {
                TextField(
                    value = inputToken,
                    onValueChange = { newValue -> inputToken = newValue },
                    label = { Text(stringResource(R.string.enter_token)) },
                    placeholder = { Text(stringResource(R.string.token)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                Button(
                    onClick = {
                        tokenSaved()
                        viewModel.saveToken(inputToken)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.token_saved),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            is TokenState.SettedToken -> {
                Text(
                    text = stringResource(R.string.token_saved_change),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .clickable { viewModel.changeToken() }
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
