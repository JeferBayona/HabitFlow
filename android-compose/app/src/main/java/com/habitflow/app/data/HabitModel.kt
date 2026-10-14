package com.habitflow.app.data

import java.time.LocalDate

data class Habit(
    val id: String = "hab_" + System.currentTimeMillis(),
    val name: String,
    val category: HabitCategory,
    val emoji: String,
    val colorHex: Long,
    val startDate: String = LocalDate.now().toString(), // Always current date
    val logs: MutableMap<String, Boolean> = mutableMapOf()
) {
    fun isCompletedOn(date: LocalDate): Boolean = logs[date.toString()] == true
    
    fun toggleDay(date: LocalDate) {
        val key = date.toString()
        if (logs[key] == true) logs.remove(key) else logs[key] = true
    }
    
    fun currentStreak(): Int {
        var streak = 0
        var date = LocalDate.now()
        // Start from today, if not done check yesterday
        if (!isCompletedOn(date)) {
            date = date.minusDays(1)
            if (!isCompletedOn(date)) return 0
        }
        while (isCompletedOn(date)) {
            streak++
            date = date.minusDays(1)
        }
        return streak
    }
    
    fun bestStreak(): Int {
        val sortedDates = logs.keys.filter { logs[it] == true }.sorted()
        if (sortedDates.isEmpty()) return 0
        var best = 1
        var current = 1
        for (i in 1 until sortedDates.size) {
            val prev = LocalDate.parse(sortedDates[i - 1])
            val cur = LocalDate.parse(sortedDates[i])
            if (cur == prev.plusDays(1)) {
                current++
                if (current > best) best = current
            } else {
                current = 1
            }
        }
        return maxOf(best, currentStreak())
    }
}

enum class HabitCategory(val label: String, val emoji: String) {
    HEALTH("Health", "❤️"),
    FITNESS("Fitness", "💪"),
    MINDFULNESS("Mind", "🧠"),
    PRODUCTIVITY("Work", "🚀"),
    LEARNING("Growth", "📖"),
    FINANCE("Finance", "🐷")
}
