package com.klmpk5.daycare_app.repository



import com.klmpk5.daycare_app.data.local.dao.ChildDao
import com.klmpk5.daycare_app.data.local.entities.Child
import kotlinx.coroutines.flow.Flow

class ChildRepository(private val childDao: ChildDao) {

    fun getAllChildren(): Flow<List<Child>> = childDao.getAllChildren()

    suspend fun insertChild(child: Child) {
        childDao.insertChild(child)
    }

    suspend fun deleteChild(child: Child) {
        childDao.deleteChild(child)
    }
}