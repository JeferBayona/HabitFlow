package com.habitflow.app.data

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitRepository(application)
    
    val habits = mutableStateListOf<Habit>()
    val selectedCategory = mutableStateOf<HabitCategory?>(null) // null = All
    val weekOffset = mutableStateOf(0) // 0 = current week
    
    init {
        val loaded = repository.loadHabits()
        habits.addAll(loaded)
    }
    
    fun getFilteredHabits(): List<Habit> {
        val cat = selectedCategory.value
        return if (cat == null) habits.toList()
        else habits.filter { it.category == cat }
    }
    
    fun getWeekDates(): List<LocalDate> {
        val today = LocalDate.now()
        val endDate = today.plusDays((weekOffset.value * 7).toLong())
        return (6 downTo 0).map { endDate.minusDays(it.toLong()) }
    }
    
    fun toggleHabitDay(habitId: String, date: LocalDate) {
        val index = habits.indexOfFirst { it.id == habitId }
        if (index < 0) return
        val habit = habits[index]
        habit.toggleDay(date)
        // Force recomposition by replacing the item
        habits[index] = habit.copy(logs = HashMap(habit.logs))
        save()
    }
    
    fun addHabit(habit: Habit) {
        // startDate is always set to LocalDate.now() by default in the Habit constructor
        habits.add(habit)
        save()
    }
    
    fun addFromTemplate(template: HabitTemplates.Template) {
        val habit = Habit(
            id = "hab_" + System.currentTimeMillis(),
            name = template.name,
            category = template.category,
            emoji = template.emoji,
            colorHex = template.colorHex,
            startDate = LocalDate.now().toString()
        )
        habits.add(habit)
        save()
    }
    
    fun deleteHabit(habitId: String) {
        habits.removeAll { it.id == habitId }
        save()
    }
    
    fun updateHabit(updated: Habit) {
        val index = habits.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            habits[index] = updated
            save()
        }
    }
    
    fun shiftWeek(delta: Int) {
        val newOffset = weekOffset.value + delta
        if (newOffset > 0) return
        weekOffset.value = newOffset
    }
    
    fun resetToCurrentWeek() {
        weekOffset.value = 0
    }
    
    fun selectCategory(category: HabitCategory?) {
        selectedCategory.value = category
    }
    
    // KPI calculations
    fun todayCompletedCount(): Int {
        val today = LocalDate.now()
        return habits.count { it.isCompletedOn(today) }
    }
    
    fun todayPercentage(): Int {
        if (habits.isEmpty()) return 0
        return (todayCompletedCount() * 100) / habits.size
    }
    
    fun topActiveStreak(): Pair<Int, String> {
        var best = 0
        var name = "No active streaks"
        habits.forEach {
            val s = it.currentStreak()
            if (s > best) {
                best = s
                name = it.name
            }
        }
        return Pair(best, name)
    }
    
    fun allTimeBestStreak(): Int {
        return habits.maxOfOrNull { it.bestStreak() } ?: 0
    }
    
    private fun save() {
        repository.saveHabits(habits.toList())
    }
}
