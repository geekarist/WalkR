package me.cpele.baladr.feature.playlistgeneration

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class FitTempoDetection(context: Context) : TempoDetection {

    private val appContext = context.applicationContext

    init {
        GoogleSignIn.getLastSignedInAccount(appContext) ?: appContext.startActivity(
            Intent(
                appContext,
                FitLoginActivity::class.java
            )
        )
    }

    override suspend fun executeAsync(durationSeconds: Int): Deferred<Int> {
        val deferred = CompletableDeferred<Int>()
        GoogleSignIn.getLastSignedInAccount(appContext)?.let { account ->
            val (startTime, endTime) = record(account)
            val bpm = read(account, startTime, endTime)
            deferred.complete(bpm)
        }
        return deferred
    }

    private suspend fun record(safeAccount: GoogleSignInAccount): Pair<Long, Long> {
        val recordingClient = Fitness.getRecordingClient(appContext, safeAccount)
        val startTime = System.currentTimeMillis()
        recordingClient.subscribe(DataType.AGGREGATE_STEP_COUNT_DELTA)
        delay(TimeUnit.SECONDS.toMillis(10))
        recordingClient.unsubscribe(DataType.AGGREGATE_STEP_COUNT_DELTA)
        val endTime = System.currentTimeMillis()
        return Pair(startTime, endTime)
    }

    private fun read(
        safeAccount: GoogleSignInAccount,
        startTime: Long,
        endTime: Long
    ): Int {
        val historyClient = Fitness.getHistoryClient(appContext, safeAccount)
        val request = DataReadRequest.Builder()
            .read(DataType.AGGREGATE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()
        val result = Tasks.await(historyClient.readData(request))
        val dataSet = result.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA)
        val dataPoint = dataSet.dataPoints.maxBy { it.getEndTime(TimeUnit.MILLISECONDS) }
        val stepsInTenSecs = dataPoint?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
        return stepsInTenSecs * 6
    }
}