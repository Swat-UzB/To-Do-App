package com.example.todoapplication.data

import androidx.room.TypeConverter
import com.example.todoapplication.data.models.Priority

class Converter {
    @TypeConverter
    fun fromPriority(priority: Priority) = priority.name

    @TypeConverter
    fun toPriority(priority: String) = Priority.valueOf(priority)
}