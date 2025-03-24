package com.example.edeaf.data.repository

import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class SignUpRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef: FirebaseDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun signUp(name:String,email:String,role:String,password:String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val uid = firebaseAuth.currentUser?.uid
                val users = Users(uid,name,email, password, role)
                firebaseRef.getReference("Users").child(uid!!).setValue(users)
                emit(Resource.Success(uid))
            }catch (e:Exception){
                emit(Resource.Error(e.message?:"Unknown Error"))
            }
        }.flowOn(dispatcher)
    }
}