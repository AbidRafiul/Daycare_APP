package com.klmpk5.daycare_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.klmpk5.daycare_app.viewModel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // Memantau perubahan state dari ViewModel
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val successUid = viewModel.loginSuccessUid

    // JIKA SUCCESS UID TIDAK KOSONG, PINDAH HALAMAN
    LaunchedEffect(successUid) {
        if (successUid != null) {
            onLoginSuccess(successUid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daycare Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Kunci inputan saat loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Kunci inputan saat loading
        )

        // Munculkan teks merah kalau ada error dari Firebase
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Masuk
        Button(
            onClick = { viewModel.login(email, pass) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Kunci tombol saat sedang loading
        ) {
            if (isLoading) {
                // Munculkan animasi berputar kalau lagi loading
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Masuk")
            }
        }
    }
}