package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class AuthStateDao {
    @Query("SELECT asValue FROM AuthStateEntity LIMIT 1")
    abstract fun get(): LiveData<String?>

    fun set(authStateStr: String) {
        insert(AuthStateEntity(0, authStateStr))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(authStateEntity: AuthStateEntity)
}
