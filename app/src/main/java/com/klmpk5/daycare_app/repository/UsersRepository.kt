package com.klmpk5.daycare_app.repository

import com.klmpk5.daycare_app.data.local.dao.UserDao
import com.klmpk5.daycare_app.data.local.entities.User
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.data.remote.model.UserRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val firebaseService: FirebaseService
) {

    fun getUserByUidLocal(uid: String): Flow<User?> {
        return userDao.getUserByUid(uid)
    }

    suspend fun syncUserFromRemote(uid: String) {
        withContext(Dispatchers.IO) {
            try {
                val remoteUser = firebaseService.getUserProfile(uid)

                if (remoteUser != null) {
                    userDao.insertUser(remoteUser.toEntity())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            val updatedUser = user.copy(
                email = user.email.trim().lowercase(),
                updatedAt = System.currentTimeMillis()
            )

            userDao.insertUser(updatedUser)

            firebaseService.saveUserProfile(
                updatedUser.toRemoteDto()
            )
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            val updatedUser = user.copy(
                email = user.email.trim().lowercase(),
                updatedAt = System.currentTimeMillis()
            )

            userDao.insertUser(updatedUser)

            firebaseService.updateUserProfile(
                updatedUser.toRemoteDto()
            )
        }
    }

    private fun UserRemoteDto.toEntity(): User {
        return User(
            uid = uid,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            role = role,
            photoUrl = photoUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun User.toRemoteDto(): UserRemoteDto {
        return UserRemoteDto(
            uid = uid,
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            role = role,
            photoUrl = photoUrl,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
