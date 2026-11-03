package com.lethargicrunner.mailclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MailCleanApp() }
    }
}

@Composable
fun MailCleanApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("MailClean", style = MaterialTheme.typography.headlineLarge)
                Text("V0.1.0 — squelette", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Comptes connectés : aucun pour l'instant.\n" +
                        "Prochaine étape : connexion Gmail / Outlook, puis écran de suggestions.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
