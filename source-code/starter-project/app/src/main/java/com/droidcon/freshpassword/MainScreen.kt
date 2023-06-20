package com.droidcon.freshpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.droidcon.freshpassword.ui.PasswordFetch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    shareText: (String) -> Unit
) {
    val uiState by viewModel.uiState.observeAsState()
    val loading by viewModel.loading.observeAsState()
    PasswordFetch(
        modifier = modifier,
        uiState = uiState ?: UiState(),
        loading = loading ?: false,
        shareText = shareText,
        onClick = viewModel::fetchPassword
    )
}
