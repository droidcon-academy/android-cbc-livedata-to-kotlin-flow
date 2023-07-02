val uiState by viewModel.uiState.collectAsStateWithLifecycle()
val loading by viewModel.loading.collectAsStateWithLifecycle()

PasswordFetch(
    modifier = modifier,
    uiState = uiState,
    loading = loading,
    shareText = shareText,
    onClick = viewModel::fetchPassword
)