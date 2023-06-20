package com.droidcon.freshpassword.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidcon.freshpassword.UiState
import com.droidcon.freshpassword.ui.theme.FreshPasswordTheme

@Composable
fun PasswordFetch(
    modifier: Modifier = Modifier,
    uiState: UiState,
    loading: Boolean,
    shareText: (String) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = uiState.password,
                    style = MaterialTheme.typography.displayMedium
                )
                if (uiState.password.isNotEmpty()) {
                    Button(onClick = { shareText(uiState.password) }) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = onClick,
                enabled = !loading
            ) {
                Text("Fresh Password")
            }
            if (uiState.history.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = "History",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        items(uiState.history.size) { i ->
                            Text(modifier = Modifier.padding(8.dp), text = uiState.history[i])
                        }
                    }
                }
            }

        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultLoadingPreview() {
    FreshPasswordTheme {
        PasswordFetch(uiState = UiState(), loading = true)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPasswordPreview() {
    FreshPasswordTheme {
        PasswordFetch(uiState = UiState("Password123", history = emptyList()), loading = false)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPasswordWithHistoryPreview() {
    FreshPasswordTheme {
        PasswordFetch(
            uiState = UiState(
                "Unicorn",
                history = listOf("Password123", "Password1234", "Password12345")
            ),
            loading = false
        )
    }
}