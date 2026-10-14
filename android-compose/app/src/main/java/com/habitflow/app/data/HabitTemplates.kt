package com.habitflow.app.data

object HabitTemplates {
    data class Template(
        val name: String,
        val category: HabitCategory,
        val emoji: String,
        val colorHex: Long
    )
    
    val templates = listOf(
        // Fitness
        Template("🚴 Biking", HabitCategory.FITNESS, "🚴", 0xFF10B981),
        Template("🏃 Running", HabitCategory.FITNESS, "🏃", 0xFF06B6D4),
        Template("🏋️ Gym Workout", HabitCategory.FITNESS, "🏋️", 0xFFF59E0B),
        Template("🧘 Yoga", HabitCategory.FITNESS, "🧘", 0xFFA855F7),
        Template("🏊 Swimming", HabitCategory.FITNESS, "🏊", 0xFF06B6D4),
        // Health  
        Template("💧 Drink 2.5L Water", HabitCategory.HEALTH, "💧", 0xFF06B6D4),
        Template("🥗 Eat Healthy", HabitCategory.HEALTH, "🥗", 0xFF10B981),
        Template("😴 Sleep 8 Hours", HabitCategory.HEALTH, "😴", 0xFF6366F1),
        Template("🌙 No Screens Before Bed", HabitCategory.HEALTH, "🌙", 0xFFF43F5E),
        Template("💊 Take Vitamins", HabitCategory.HEALTH, "💊", 0xFFF59E0B),
        // Mindfulness
        Template("🧘 Meditate 15 min", HabitCategory.MINDFULNESS, "🧘", 0xFFA855F7),
        Template("📓 Journaling", HabitCategory.MINDFULNESS, "📓", 0xFF6366F1),
        Template("🙏 Gratitude Practice", HabitCategory.MINDFULNESS, "🙏", 0xFFF59E0B),
        // Productivity
        Template("📧 Zero Inbox", HabitCategory.PRODUCTIVITY, "📧", 0xFFF59E0B),
        Template("📋 Plan Tomorrow", HabitCategory.PRODUCTIVITY, "📋", 0xFF6366F1),
        Template("🎯 Deep Work 2hrs", HabitCategory.PRODUCTIVITY, "🎯", 0xFFF43F5E),
        // Learning
        Template("📚 Read 25 Pages", HabitCategory.LEARNING, "📚", 0xFF6366F1),
        Template("💻 Practice Coding", HabitCategory.LEARNING, "💻", 0xFF10B981),
        Template("🎸 Practice Instrument", HabitCategory.LEARNING, "🎸", 0xFFF59E0B),
        Template("🗣️ Learn Language", HabitCategory.LEARNING, "🗣️", 0xFFA855F7),
        // Finance
        Template("💰 Track Expenses", HabitCategory.FINANCE, "💰", 0xFF10B981),
        Template("🚫 No Impulse Buying", HabitCategory.FINANCE, "🚫", 0xFFF43F5E),
        Template("📊 Check Investments", HabitCategory.FINANCE, "📊", 0xFF06B6D4)
    )
}
