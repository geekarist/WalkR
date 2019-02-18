package me.cpele.baladr.feature.playlistgeneration

import android.app.Activity
import kotlinx.coroutines.Deferred

interface TempoDetection {
    suspend fun executeAsync(durationSeconds: Int): Deferred<Int>
    fun setup(activity: Activity)
}