package com.example.edeaf.data.repository

import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.ui.util.SharedPreference
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await

class LoginRepository(
    private val firebaseAuth: FirebaseAuth,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun login(email: String,password:String): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading())
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
                emit(Resource.Success(firebaseAuth.uid!!))
            }catch (e: Exception){
                emit(Resource.Error(e.message!!))
            }
        }.flowOn(dispatcher)
    }

}