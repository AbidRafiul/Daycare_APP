package com.klmpk5.daycare_app.data.remote.datasource

import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto


class ChildRemoteDataSource(private val firebaseService: FirebaseService) {

    suspend fun addChild(child: ChildRemoteDto) {
        firebaseService.addChild(child)
    }

    suspend fun getChildrenByParent(parentUserId: String): List<ChildRemoteDto> {
        return firebaseService.getChildrenByParent(parentUserId)
    }

    suspend fun syncChildrenToFirebase(children: List<ChildRemoteDto>) {
        firebaseService.syncChildrenToFirebase(children)
    }
}