package me.cpele.baladr

import android.content.Context
import com.facebook.stetho.Stetho

object StethoWrapper {
    fun initializeWithDefaults(context: Context) {
        Stetho.initializeWithDefaults(context)
    }
}
