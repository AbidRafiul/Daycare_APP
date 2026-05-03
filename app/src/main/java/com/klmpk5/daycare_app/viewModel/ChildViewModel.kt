package com.klmpk5.daycare_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChildViewModel(
    private val childRepository: ChildRepository
) : ViewModel() {

    // Simple Flow dari Room, otomatis memantau perubahan data lokal
    val children: Flow<List<Child>> = childRepository.getAllChildrenLocal()

    // BLOK INIT: Otomatis dipanggil 1x saat MainActivity membuka ViewModel ini
    init {
        // Kita panggil fungsi sinkronisasi secara background
        viewModelScope.launch {
            // Gunakan ID "PARENT_999" yang kemarin kita pakai saat testing agar ada datanya
            // Nanti, ID ini bisa diganti dinamis sesuai siapa yang sedang Login
            syncChildren("PARENT_999")
        }
    }

    // Fungsi suspend untuk menarik data (Dipanggil oleh init atau tombol Refresh)
    suspend fun syncChildren(parentUserId: String) {
        childRepository.syncChildrenFromRemote(parentUserId)
    }

    // Mengubah fungsi ini agar langsung menggunakan viewModelScope,
    // sehingga tim UI tinggal panggil viewModel.addChild(data) tanpa pusing mikirin thread
    fun addChild(child: Child) {
        viewModelScope.launch {
            childRepository.addChild(child)
        }
    }
}

class ChildViewModelFactory(
    private val childRepository: ChildRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChildViewModel::class.java)) {
            return ChildViewModel(childRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}