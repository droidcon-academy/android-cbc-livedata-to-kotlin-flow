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
    val password by viewModel.password.collectAsStateWithLifecycle()
    val previousPasswords by viewModel.previousPasswords.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    PasswordFetch(
        modifier = modifier,
        password = password,
        previousPasswords = previousPasswords,
        loading = loading,
        shareText = shareText,
        onClick = viewModel::fetchPassword
    )
}
