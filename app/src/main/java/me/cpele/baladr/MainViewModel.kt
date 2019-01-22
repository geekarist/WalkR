package me.cpele.baladr

import androidx.lifecycle.*

class MainViewModel(authStateRepository: AuthStateRepository) : ViewModel() {

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
}
