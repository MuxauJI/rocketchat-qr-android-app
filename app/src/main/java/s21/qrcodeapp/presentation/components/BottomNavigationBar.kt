package s21.qrcodeapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import s21.qrcodeapp.R

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.qr_code)
                )
            },
            label = { Text(stringResource(R.string.qr_code)) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.token)
                )
            },
            label = { Text(stringResource(R.string.token)) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
    }
}