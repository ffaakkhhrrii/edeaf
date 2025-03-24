package com.example.edeaf.data.repository

import android.util.Log
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.model.DictionaryModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class DictionaryRepository(
    private val firebaseDatabase: FirebaseDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getAllDictionary(type: String): Flow<Resource<List<DictionaryModel>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val data = firebaseDatabase.getReference("Kamus").orderByChild("jenis").equalTo(type).get().await()
                if (data.exists()) {
                    val dictionaryList = data.children.mapNotNull {
                        it.getValue(DictionaryModel::class.java)
                    }

                    if (dictionaryList.isNotEmpty()) {
                        emit(Resource.Success(dictionaryList))
                    } else {
                        emit(Resource.Error("Empty Data"))
                    }
                } else {
                    emit(Resource.Error("Empty Data"))
                }
            }catch (e:Exception){
                emit(Resource.Error(e.message?: "Unknown Error"))
            }
        }.flowOn(dispatcher)
    }

}