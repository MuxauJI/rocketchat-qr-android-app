package s21.qrcodeapp.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import s21.qrcodeapp.R
import s21.qrcodeapp.presentation.components.BottomNavigationBar
import s21.qrcodeapp.presentation.components.QRCodeScreen
import s21.qrcodeapp.presentation.components.TokenScreen
import s21.qrcodeapp.presentation.state.StatusState
import s21.qrcodeapp.presentation.state.TokenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: QRCodeViewModel = hiltViewModel()) {
    var selectedTab by remember {
        mutableIntStateOf(
            if (viewModel.tokenState.value is TokenState.SettedToken) 0 else 1
        )
    }
    val statusState by viewModel.statusState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.app_name))
                        Spacer(modifier = Modifier.weight(1f))
                        when (val status = statusState) {
                            is StatusState.Loading -> {
                                Text(
                                    text = stringResource(R.string.loading),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            is StatusState.Error -> {
                                Text(
                                    text = status.error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            is StatusState.TokenSaved -> {
                                Text(
                                    text = stringResource(R.string.token_saved),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            is StatusState.ValidQR -> {
                                Text(
                                    text = stringResource(R.string.qr_valid),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            else -> {}
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> QRCodeScreen(viewModel = viewModel)
                1 -> TokenScreen(viewModel = viewModel) { selectedTab = 0 }
            }
        }
    }
}



