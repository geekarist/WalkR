package me.cpele.baladr

import androidx.room.TypeConverter
import com.google.gson.Gson

class ListTypeConverter {

    @TypeConverter
    fun toList(json: String): List<String> {
        val type = ArrayList<String>().javaClass
        return Gson().fromJson(json, type)
    }

    @TypeConverter
    fun toJson(strings: List<String>): String = Gson().toJson(strings)
}
