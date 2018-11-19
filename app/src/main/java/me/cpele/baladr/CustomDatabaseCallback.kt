package me.cpele.baladr

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.nio.charset.Charset

class CustomDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        GlobalScope.launch {
            init().await()
        }
    }

    private fun init() = GlobalScope.async(Dispatchers.IO) {
        val app = CustomApp.instance
        val trackDao = app.database.trackDao()
        val rawRes = app.resources.openRawResource(R.raw.default_reco_100)
        val tracksDto = Gson().fromJson(rawRes.reader(Charset.defaultCharset()), TracksDto::class.java)
        tracksDto.tracks?.map { trackDto ->
            trackDto?.let {
                TrackBo(
                    it.id ?: TODO(),
                    it.album?.images?.get(0)?.url ?: "https://picsum.photos/200",
                    it.name ?: TODO(),
                    it.artists?.get(0)?.name ?: TODO(),
                    it.duration_ms.toString(),
                    80
                )
            } ?: TODO()
        }?.let {
            trackDao.insertAll(it)
        }
    }
}