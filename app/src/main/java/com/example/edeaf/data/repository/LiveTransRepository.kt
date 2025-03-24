package com.example.edeaf.data.repository

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.model.LiveTrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date

class LiveTransRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {

    @SuppressLint("SimpleDateFormat")
    fun teacherMakeRoom(
        title: String,
    ): Flow<Resource<String>>{
        return flow {
            emit(Resource.Loading())
            try {
                val database = firebaseDatabase.getReference("LiveTrans")
                val uid = firebaseAuth.currentUser?.uid
                val idLive = database.push().key!!

                val roomCode = (100000..999999).random()
                val currentTime = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val formattedTime = dateFormat.format(currentTime)

                val liveTrans = LiveTrans(idLive,uid!!,title,formattedTime,"", codeRoom = roomCode, historyText = "", participantId = "")

                database.child(idLive).setValue(liveTrans).await()

                emit(Resource.Success(idLive))
            }catch (e:Exception){
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }
    }

}