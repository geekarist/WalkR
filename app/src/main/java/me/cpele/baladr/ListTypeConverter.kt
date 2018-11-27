package me.cpele.baladr

import androidx.room.TypeConverter
import com.google.gson.Gson

class ListTypeConverter {

    @TypeConverter
    fun toList(json: String): List<String> {
        val type = listOf("yo").javaClass
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(strings: List<String>) = Gson().toJson(strings)
}
