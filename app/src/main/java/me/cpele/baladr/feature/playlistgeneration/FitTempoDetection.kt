package me.cpele.baladr.feature.playlistgeneration

import android.app.Activity
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.concurrent.TimeUnit

class FitTempoDetection(context: Context) : TempoDetection {

    private val appContext = context.applicationContext

    override fun setup(activity: Activity) {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(appContext)

        if (lastSignedInAccount == null) {
            val options = GoogleSignInOptions.Builder().requestEmail().build()
            val client = GoogleSignIn.getClient(appContext, options)
            val intent = client.signInIntent
            activity.startActivityForResult(intent, 0)
        }
    }

    override suspend fun executeAsync(durationSeconds: Int): Deferred<Int> {
        val deferred = CompletableDeferred<Int>()

        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(appContext)

        lastSignedInAccount?.let { safeAccount ->
            val client = Fitness.getHistoryClient(appContext, safeAccount)
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