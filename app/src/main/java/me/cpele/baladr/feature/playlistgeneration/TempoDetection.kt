package me.cpele.baladr.feature.playlistgeneration

import kotlinx.coroutines.Deferred

interface TempoDetection {
    suspend fun executeAsync(durationSeconds: Int): Deferred<Int>
}