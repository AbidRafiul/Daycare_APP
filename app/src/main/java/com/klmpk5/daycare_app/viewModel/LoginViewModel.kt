package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set
    var loginSuccessUid by mutableStateOf<String?>(null)
        private set

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            errorMessage = "Email dan Password tidak boleh kosong!"
            return
        }
        isLoading = true
        errorMessage = ""

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    loginSuccessUid = auth.currentUser?.uid
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Gagal login."
                }
            }
    }

    fun resetState() {
        loginSuccessUid = null
        errorMessage = ""
    }
}