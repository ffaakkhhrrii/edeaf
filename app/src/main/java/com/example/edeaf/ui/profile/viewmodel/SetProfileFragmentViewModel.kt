package com.example.edeaf.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SetProfileFragmentViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel(){

    fun changeProfile(uid:String,name: String,email:String,password:String,role:String): Flow<Resource<String>>{
        return repository.changeProfile(
            uid, name, email, password, role
        )
    }

    fun sendEmailUpdate(email:String): Flow<Resource<String>>{
        return repository.sendEmailVerification(email)
    }

}