package me.cpele.baladr

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.cpele.baladr.common.database.AuthStateDao
import net.openid.appauth.AuthState

class AuthStateRepository(private val authStateDao: AuthStateDao) {

    fun set(authState: AuthState) {
        val serialized = authState.jsonSerializeString()
        authStateDao.set(serialized)
    }

    fun get(): LiveData<AuthState?> = Transformations.map(authStateDao.get()) {
        it?.let { serialized -> AuthState.jsonDeserialize(serialized) }
    }

    fun clear() = set(AuthState())
}
