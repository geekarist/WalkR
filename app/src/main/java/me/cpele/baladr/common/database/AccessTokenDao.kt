package me.cpele.baladr.common.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class AccessTokenDao {
    @Query("SELECT atValue FROM AccessTokenEntity LIMIT 1")
    abstract fun get(): LiveData<String?>

    fun set(accessToken: String) {
        insert(AccessTokenEntity(0, accessToken))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(accessTokenEntity: AccessTokenEntity)
}
