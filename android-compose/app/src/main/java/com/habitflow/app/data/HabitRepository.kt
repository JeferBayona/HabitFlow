package com.habitflow.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class HabitRepository(context: Context) {
    private val prefs = context.getSharedPreferences("habitflow_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun loadHabits(): MutableList<Habit> {
        val json = prefs.getString("habits", null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            gson.fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }
    
    fun saveHabits(habits: List<Habit>) {
        prefs.edit().putString("habits", gson.toJson(habits)).apply()
    }
}
