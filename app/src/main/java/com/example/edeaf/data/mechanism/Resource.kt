package com.example.edeaf.data.mechanism

import kotlinx.coroutines.flow.Flow

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Idle<T>: Resource<T>()
}

suspend fun <T> Flow<Resource<T>>.handleCollect(
    onSuccess: (resource: Resource<T>) -> Unit,
    onError: (resource: Resource<T>) -> Unit,
    onLoading: (resource: Resource<T>) -> Unit = {},
    onIdle: () -> Unit = {}
) {
    this.collect { resource ->
        when (resource) {
            is Resource.Success -> onSuccess(resource)
            is Resource.Idle -> onIdle()
            is Resource.Error -> onError(resource)
            is Resource.Loading -> onLoading(resource)
        }
    }
}