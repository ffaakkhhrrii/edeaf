package com.example.edeaf.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.model.Users
import com.example.edeaf.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class UserProfileFragmentViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel(){

    fun showDataProfile(): Flow<Resource<Users>>{
        return repository.showDataProfile()
    }

    fun logout(): Flow<Resource<String>>{
        return repository.logout()
    }
}