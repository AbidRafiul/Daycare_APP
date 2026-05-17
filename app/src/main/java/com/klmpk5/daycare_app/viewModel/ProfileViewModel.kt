package com.klmpk5.daycare_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.klmpk5.daycare_app.data.local.entities.User
import com.klmpk5.daycare_app.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * State untuk proses update profile.
 */
sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}

/**
 * State untuk proses ubah password.
 */
sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _profileUpdateState =
        MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

    private val _changePasswordState =
        MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    /**
     * Mengambil UID user yang sedang login.
     */
    fun getCurrentUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Mengambil email user yang sedang login.
     */
    fun getCurrentEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Mengambil data user dari Room lokal berdasarkan UID.
     *
     * Biasanya dipakai di ProfileScreen/EditProfileScreen.
     */
    fun getUserProfile(uid: String): Flow<User?> {
        return userRepository.getUserByUidLocal(uid)
    }

    /**
     * Sync data user dari Firestore ke Room.
     *
     * Dipanggil saat masuk halaman profile.
     */
    fun syncUserProfile(uid: String) {
        viewModelScope.launch {
            userRepository.syncUserFromRemote(uid)
        }
    }

    /**
     * Update profile parent.
     *
     * Untuk kebutuhan kamu:
     * - user hanya bisa edit nama
     * - user hanya bisa edit no HP
     *
     * Email dan role tidak diubah dari screen edit profile.
     */
    fun updateProfile(
        currentUser: User,
        fullName: String,
        phoneNumber: String
    ) {
        if (fullName.isBlank()) {
            _profileUpdateState.value =
                ProfileUpdateState.Error("Nama tidak boleh kosong")
            return
        }

        if (phoneNumber.isBlank()) {
            _profileUpdateState.value =
                ProfileUpdateState.Error("No HP tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            try {
                _profileUpdateState.value = ProfileUpdateState.Loading

                val updatedUser = currentUser.copy(
                    fullName = fullName.trim(),
                    phoneNumber = phoneNumber.trim(),
                    updatedAt = System.currentTimeMillis()
                )

                /**
                 * Repository akan menyimpan ke:
                 * - Room lokal
                 * - Firestore collection users
                 */
                userRepository.updateUser(updatedUser)

                _profileUpdateState.value = ProfileUpdateState.Success
            } catch (e: Exception) {
                _profileUpdateState.value = ProfileUpdateState.Error(
                    e.localizedMessage ?: "Gagal memperbarui profile"
                )
            }
        }
    }

    /**
     * Ubah password menggunakan Firebase Authentication.
     *
     * Firebase butuh re-authenticate dulu menggunakan password lama.
     */
    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _changePasswordState.value =
                ChangePasswordState.Error("Semua field password wajib diisi")
            return
        }

        if (newPassword.length < 6) {
            _changePasswordState.value =
                ChangePasswordState.Error("Password baru minimal 6 karakter")
            return
        }

        if (newPassword != confirmPassword) {
            _changePasswordState.value =
                ChangePasswordState.Error("Konfirmasi password tidak sama")
            return
        }

        viewModelScope.launch {
            try {
                _changePasswordState.value = ChangePasswordState.Loading

                val firebaseUser = auth.currentUser
                val email = firebaseUser?.email

                if (firebaseUser == null || email.isNullOrBlank()) {
                    _changePasswordState.value =
                        ChangePasswordState.Error("User tidak ditemukan")
                    return@launch
                }

                /**
                 * Re-authenticate user dengan password lama.
                 */
                val credential = EmailAuthProvider.getCredential(
                    email,
                    oldPassword
                )

                firebaseUser.reauthenticate(credential).await()

                /**
                 * Update password baru di Firebase Auth.
                 */
                firebaseUser.updatePassword(newPassword).await()

                _changePasswordState.value = ChangePasswordState.Success
            } catch (e: Exception) {
                _changePasswordState.value = ChangePasswordState.Error(
                    e.localizedMessage ?: "Gagal mengubah password"
                )
            }
        }
    }

    /**
     * Logout akun parent.
     *
     * Untuk popup alert-nya nanti ditangani di ProfileScreen.
     */
    fun logout() {
        auth.signOut()
    }

    fun resetProfileUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }
}

/**
 * Factory karena ProfileViewModel membutuhkan UserRepository.
 */
class ProfileViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}