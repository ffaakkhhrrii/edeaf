package com.example.edeaf.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edeaf.data.mechanism.Resource
import com.example.edeaf.data.repository.DictionaryRepository
import com.example.edeaf.data.model.DictionaryModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryFragmentViewModel @Inject constructor(
    private val repository: DictionaryRepository
):ViewModel() {
    private var _listDictionary = MutableStateFlow<Resource<List<DictionaryModel>>>(Resource.Idle())
    val listDictionary : StateFlow<Resource<List<DictionaryModel>>> = _listDictionary

    fun fetchDictionary(type: String){
        viewModelScope.launch {
            repository.getAllDictionary(type).collect{ result->
                _listDictionary.value = result
            }
        }
    }
}