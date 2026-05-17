package com.klmpk5.daycare_app.viewModel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_app.data.local.entities.User
import com.klmpk5.daycare_app.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    var successMessage by mutableStateOf("")
        private set

    var loginSuccessUid by mutableStateOf<String?>(null)
        private set

    var loginSuccessEmail by mutableStateOf<String?>(null)
        private set

    fun login(email: String, pass: String) {
        val normalizedEmail = email.trim().lowercase()

        if (normalizedEmail.isBlank() || pass.isBlank()) {
            errorMessage = "Email dan password tidak boleh kosong!"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            errorMessage = "Format email belum valid."
            return
        }

        isLoading = true
        errorMessage = ""
        successMessage = ""

        auth.signInWithEmailAndPassword(normalizedEmail, pass)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    loginSuccessUid = uid
                    loginSuccessEmail = auth.currentUser?.email?.lowercase() ?: normalizedEmail

                    if (uid != null) {
                        viewModelScope.launch {
                            userRepository.syncUserFromRemote(uid)
                        }
                    }
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Gagal login."
                }
            }
    }

    fun registerParent(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ) {
        val normalizedEmail = email.trim().lowercase()

        if (
            fullName.isBlank() ||
            normalizedEmail.isBlank() ||
            phoneNumber.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank()
        ) {
            errorMessage = "Semua field wajib diisi."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            errorMessage = "Format email belum valid."
            return
        }

        if (password.length < 6) {
            errorMessage = "Password minimal 6 karakter."
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Konfirmasi password tidak sama."
            return
        }

        isLoading = true
        errorMessage = ""
        successMessage = ""

        auth.createUserWithEmailAndPassword(normalizedEmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid

                    if (uid == null) {
                        isLoading = false
                        errorMessage = "Gagal membuat akun."
                        return@addOnCompleteListener
                    }

                    val now = System.currentTimeMillis()

                    val user = User(
                        uid = uid,
                        fullName = fullName.trim(),
                        email = normalizedEmail,
                        phoneNumber = phoneNumber.trim(),
                        role = "parent",
                        photoUrl = null,
                        createdAt = now,
                        updatedAt = now
                    )

                    viewModelScope.launch {
                        try {
                            userRepository.saveUser(user)

                            isLoading = false
                            successMessage = "Akun berhasil dibuat. Silakan login."

                            auth.signOut()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = e.localizedMessage ?: "Gagal menyimpan profil user."
                        }
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.localizedMessage ?: "Gagal register."
                }
            }
    }

    fun sendResetPassword(email: String) {
        val normalizedEmail = email.trim().lowercase()

        if (normalizedEmail.isBlank()) {
            errorMessage = "Masukkan email terlebih dahulu."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            errorMessage = "Format email belum valid."
            return
        }

        isLoading = true
        errorMessage = ""
        successMessage = ""

        auth.sendPasswordResetEmail(normalizedEmail)
            .addOnCompleteListener { task ->
                isLoading = false

                if (task.isSuccessful) {
                    successMessage = "Link reset password sudah dikirim ke email."
                } else {
                    errorMessage = task.exception?.localizedMessage ?: "Gagal mengirim reset password."
                }
            }
    }

    fun resetState() {
        loginSuccessUid = null
        loginSuccessEmail = null
        errorMessage = ""
        successMessage = ""
        isLoading = false
    }

    class Factory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
