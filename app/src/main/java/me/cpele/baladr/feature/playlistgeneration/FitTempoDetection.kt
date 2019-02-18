package me.cpele.baladr.feature.playlistgeneration

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class FitTempoDetection(private val context: Context) : TempoDetection {
    override suspend fun executeAsync(durationSeconds: Int): Deferred<Int> {
        val deferred = CompletableDeferred<Int>()
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            delay(TimeUnit.SECONDS.toMillis(durationSeconds.toLong()))
            val client = Fitness.getHistoryClient(context, account)
            val now = System.currentTimeMillis()
            val tenSecsAgo = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(10)
            val request = DataReadRequest.Builder()
                .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setTimeRange(tenSecsAgo, now, TimeUnit.MILLISECONDS)
                .build()
            val result = Tasks.await(client.readData(request))
            val dataSet = result.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
            val dataPoint = dataSet.dataPoints[0]
            deferred.complete(dataPoint.getValue(Field.FIELD_STEPS).asInt())
        }
        return deferred
    }
}