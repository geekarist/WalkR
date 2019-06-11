package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainViewModel(private val authStateRepository: AuthStateRepository) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

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
