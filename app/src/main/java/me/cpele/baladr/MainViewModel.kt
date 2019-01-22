package me.cpele.baladr

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val authStateRepository: AuthStateRepository) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    class Factory(private val authStateRepository: AuthStateRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.cast(MainViewModel(authStateRepository)) as T
        }
    }

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    val isLoginVisible: LiveData<Boolean> = Transformations.map(authStateRepository.get()) { it?.isAuthorized != true }

    val isLogoutVisible: LiveData<Boolean> = Transformations.map(isLoginVisible) { !it }

    fun postTitle(strTitle: String) {
        _title.value = strTitle
    }

    fun logout() = launch { authStateRepository.clear() }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}
