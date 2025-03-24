package com.example.edeaf.ui.signup

import androidx.lifecycle.ViewModel
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SignupFragmentViewModel @Inject constructor(
    private val repository: SignUpRepository
): ViewModel() {

    fun signUp(name:String,email:String,role:String,password:String): Flow<Resource<String>>{
        return repository.signUp(name, email, role, password)
    }

}