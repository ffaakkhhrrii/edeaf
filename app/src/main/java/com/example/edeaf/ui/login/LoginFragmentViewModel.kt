package com.example.edeaf.ui.login

import androidx.lifecycle.ViewModel
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    suspend fun login(
        email:String,
        password:String
    ): Flow<Resource<String>> {
        return repository.login(email,password)
    }
}