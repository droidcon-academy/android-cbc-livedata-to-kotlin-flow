package com.droidcon.freshpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.droidcon.freshpassword.ui.PasswordFetch

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    shareText: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    PasswordFetch(
        modifier = modifier,
        uiState = uiState,
        loading = loading,
        shareText = shareText,
        onClick = viewModel::fetchPassword
    )
}
