package com.klmpk5.daycare_app.repository



import com.klmpk5.daycare_app.data.local.dao.ChildDao
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.remote.datasource.ChildRemoteDataSource
import com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto
import kotlinx.coroutines.flow.Flow

class ChildRepository(
    private val childDao: ChildDao,
    private val remoteDataSource: ChildRemoteDataSource
) {

    // Mendapatkan data anak dari Room
    fun getAllChildren(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    // Menambahkan data anak ke Room dan Firebase
    suspend fun insertChild(child: Child) {
        // Menyimpan data ke Room terlebih dahulu
        childDao.insertChild(child)

        // Mengkonversi data Child menjadi DTO untuk Firebase
        val childDto = ChildRemoteDto(
            fullName = child.fullName,
            birthDate = child.birthDate,
            gender = child.gender,
            parentUserId = child.parentUserId
        )

        // Menyimpan data ke Firebase
        remoteDataSource.addChild(childDto)
    }

    // Sinkronisasi data dari Room ke Firebase
    suspend fun syncChildrenToFirebase(children: List<Child>) {
        val childrenDto = children.map {
            ChildRemoteDto(
                fullName = it.fullName,
                birthDate = it.birthDate,
                gender = it.gender,
                parentUserId = it.parentUserId
            )
        }
        remoteDataSource.syncChildrenToFirebase(childrenDto)
    }

    // Mendapatkan data anak dari Firebase dan menyimpannya ke Room
    suspend fun getChildrenFromFirebase(parentUserId: String) {
        val remoteChildren = remoteDataSource.getChildrenByParent(parentUserId)

        // Menyimpan data Firebase ke Room
        remoteChildren.forEach {
            childDao.insertChild(it.toEntity()) // Memanggil toEntity() dari ChildRemoteDto
        }
    }
}