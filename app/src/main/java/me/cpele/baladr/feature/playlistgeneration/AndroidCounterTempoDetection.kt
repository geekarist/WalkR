package me.cpele.baladr.feature.playlistgeneration

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class AndroidCounterTempoDetection(
    private val tempoDetectionAsync: AndroidCounterTempoDetectionAsync
) : TempoDetection {

    override suspend fun execute(durationSeconds: Int): Int {

        val durationMillis = TimeUnit.SECONDS.toMillis(durationSeconds + 3L)

        return withTimeout(durationMillis) {

            suspendCancellableCoroutine { continuation: CancellableContinuation<Int> ->

                continuation.invokeOnCancellation { tempoDetectionAsync.cancel() }

                tempoDetectionAsync.execute(durationSeconds) {
                    continuation.resume(it)
                }
            }
        }
    }
}

