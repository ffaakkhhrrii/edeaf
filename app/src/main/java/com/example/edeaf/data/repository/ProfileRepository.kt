package com.example.edeaf.data.repository

import android.util.Log
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await

class ProfileRepository (
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseUser: FirebaseUser
){
    fun showDataProfile(): Flow<Resource<Users>>{
        return flow {
            emit(Resource.Loading())
            try {
                val uid = firebaseAuth.currentUser?.uid
                val database = firebaseDatabase.getReference("Users").child(uid.toString())
                val snapshot = database.get().await()
                if (snapshot.exists()){
                    val data = snapshot.getValue(Users::class.java)
                    emit(Resource.Success(data!!))
                }else{
                    emit(Resource.Error("Empty Data"))
                }
            }catch (e:Exception){
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }
    }

    fun logout():Flow<Resource<String>>{
        return flow {
            emit(Resource.Loading())
            try{
                firebaseAuth.signOut()
                emit(Resource.Success("Logout"))
            }catch (e:Exception){
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }
    }

    fun changeProfile(uid:String,name: String,email:String,password:String,role:String): Flow<Resource<String>>{
        return flow {
            emit(Resource.Loading())
            try {
                val users = Users(uid, name, email, password, role)
                firebaseDatabase.getReference("Users").child(uid).setValue(users).await()

                emit(Resource.Success("Change Profile Success"))
            }catch (e:Exception){
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }
    }

    fun sendEmailVerification(email:String): Flow<Resource<String>>{
        return flow {
            emit(Resource.Loading())
            try {
                supervisorScope {
                    firebaseUser.verifyBeforeUpdateEmail(email)
                    firebaseUser.sendEmailVerification().await()
                    Log.e("updateEmailError", "Send email success")
                    emit(Resource.Success("Send Email Success"))
                }
            }catch (e:Exception){
                Log.e("updateEmailError",e.message?: "Unknown Error")
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }
    }
}