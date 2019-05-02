package me.cpele.baladr.feature.playlistgeneration

interface TempoDetection {
    suspend fun execute(durationSeconds: Int): Int
}