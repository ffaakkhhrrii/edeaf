package com.example.edeaf.ui.livetrans.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.repository.LiveTransRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveTranslationTeacherViewModel @Inject constructor(
    private val repository: LiveTransRepository
):ViewModel() {

    fun makeRoom(title:String): Flow<Resource<String>> {
        return repository.teacherMakeRoom(title)
    }

}