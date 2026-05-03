package com.klmpk5.daycare_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.repository.ChildRepository
import kotlinx.coroutines.flow.Flow

class ChildViewModel(
    private val childRepository: ChildRepository
) : ViewModel() {

    // Simple Flow dari Room, tanpa UiState
    val children: Flow<List<Child>> = childRepository.getAllChildrenLocal()

    suspend fun syncChildren(parentUserId: String) {
        childRepository.syncChildrenFromRemote(parentUserId)
    }

    suspend fun addChild(child: Child) {
        childRepository.addChild(child)
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

