package me.cpele.baladr.common.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.cpele.baladr.CustomApp
import me.cpele.baladr.common.datasource.TracksDto
import java.nio.charset.Charset

class DatabasePopulationCallback : RoomDatabase.Callback() {

    private val app by lazy { CustomApp.instance }
    private val gson by lazy { Gson() }
    private val trackDao by lazy { app.database.trackDao() }

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        GlobalScope.launch {
            init().await()
        }
    }

    private fun init() = GlobalScope.async(Dispatchers.IO) {
        for (tempo in 70..200 step 2) {
            insertDefaultTracks(tempo)
        }
    }

    private fun insertDefaultTracks(tempo: Int) {
        val rawRes = app.resources.openRawResource(
            app.resources.getIdentifier(
                "default_reco_$tempo",
                "raw",
                app.packageName
            )
        )
        val tracksDto = gson.fromJson(
            rawRes.reader(Charset.defaultCharset()),
            TracksDto::class.java
        )
        tracksDto.tracks?.map { trackDto ->
            trackDto?.let {
                val url = it.album?.images?.last()?.url ?: "https://picsum.photos/200"
                Glide.with(app)
                    .asBitmap()
                    .load(url)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .submit()
                TrackEntity(
                    it.id ?: TODO(),
                    url,
                    it.name ?: TODO(),
                    it.artists?.get(0)?.name ?: TODO(),
                    it.duration_ms.toString(),
                    tempo,
                    it.uri ?: TODO()
                )
            } ?: TODO()
        }?.let {
            trackDao.insertAll(it)
        }
    }
}