package com.habitflow.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.app.data.*
import com.habitflow.app.ui.components.AddHabitDialog
import com.habitflow.app.ui.components.TemplateHabitsDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFlowApp(viewModel: HabitViewModel) {
    var showTemplateDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddOptions by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<Habit?>(null) }
    var deleteConfirmHabit by remember { mutableStateOf<Habit?>(null) }

    val habits = viewModel.habits
    val weekDates = viewModel.getWeekDates()
    val filteredHabits = viewModel.getFilteredHabits()
    val today = LocalDate.now()
    val todayFormatted = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "HabitFlow",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF6366F1).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
                            ) {
                                Text(
                                    "PRO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF818CF8),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Text(
                            todayFormatted,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                ),
                actions = {
                    // Add New Habit Button
                    Button(
                        onClick = { showAddOptions = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            )
        },
        containerColor = Color(0xFF020617)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // KPI Summary Cards
            KPISection(viewModel)

            // Category Filter
            CategoryFilterBar(
                selectedCategory = viewModel.selectedCategory.value,
                habitCounts = habits.groupBy { it.category }.mapValues { it.value.size },
                totalCount = habits.size,
                onSelect = { viewModel.selectCategory(it) }
            )

            // Week Navigation
            WeekNavigator(
                weekDates = weekDates,
                weekOffset = viewModel.weekOffset.value,
                onShift = { viewModel.shiftWeek(it) },
                onReset = { viewModel.resetToCurrentWeek() }
            )

            // Habit Cards List
            Text(
                "📋 Daily Check-In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (filteredHabits.isEmpty()) {
                EmptyState(
                    onAddClick = { showAddOptions = true }
                )
            } else {
                filteredHabits.forEach { habit ->
                    HabitCard(
                        habit = habit,
                        weekDates = weekDates,
                        today = today,
                        onToggle = { date -> viewModel.toggleHabitDay(habit.id, date) },
                        onEdit = { editingHabit = habit },
                        onDelete = { deleteConfirmHabit = habit }
                    )
                }
            }

            // Streak Leaderboard
            if (habits.isNotEmpty()) {
                StreakLeaderboard(habits = filteredHabits)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Add Options Bottom Sheet
    if (showAddOptions) {
        AddOptionsDialog(
            onDismiss = { showAddOptions = false },
            onTemplate = {
                showAddOptions = false
                showTemplateDialog = true
            },
            onManual = {
                showAddOptions = false
                showAddDialog = true
            }
        )
    }

    // Template Dialog
    if (showTemplateDialog) {
        TemplateHabitsDialog(
            onDismiss = { showTemplateDialog = false },
            onSelectTemplate = { template ->
                viewModel.addFromTemplate(template)
                showTemplateDialog = false
            }
        )
    }

    // Add/Edit Dialog
    if (showAddDialog || editingHabit != null) {
        AddHabitDialog(
            onDismiss = {
                showAddDialog = false
                editingHabit = null
            },
            editHabit = editingHabit,
            onSave = { name, category, emoji, color ->
                if (editingHabit != null) {
                    viewModel.updateHabit(
                        editingHabit!!.copy(
                            name = name,
                            category = category,
                            emoji = emoji,
                            colorHex = color
                        )
                    )
                } else {
                    viewModel.addHabit(
                        Habit(
                            name = name,
                            category = category,
                            emoji = emoji,
                            colorHex = color,
                            startDate = LocalDate.now().toString()
                        )
                    )
                }
                showAddDialog = false
                editingHabit = null
            }
        )
    }

    // Delete Confirmation
    if (deleteConfirmHabit != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmHabit = null },
            title = {
                Text(
                    "Delete \"${deleteConfirmHabit!!.name}\"?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("This will permanently remove this habit and its streak history.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHabit(deleteConfirmHabit!!.id)
                        deleteConfirmHabit = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF43F5E)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmHabit = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun AddOptionsDialog(
    onDismiss: () -> Unit,
    onTemplate: () -> Unit,
    onManual: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Habit", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "How would you like to add a habit?",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )

                // Template Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onTemplate),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.FlashOn,
                            "Quick Add",
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                "Quick Add from Templates",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                "Biking, Running, Meditation & more",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                // Manual Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onManual),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            "Create Custom",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                "Create Custom Habit",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                "Name your own habit from scratch",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF1E293B)
    )
}

@Composable
private fun KPISection(viewModel: HabitViewModel) {
    val todayPct = viewModel.todayPercentage()
    val todayCount = viewModel.todayCompletedCount()
    val total = viewModel.habits.size
    val (topStreak, topStreakName) = viewModel.topActiveStreak()
    val allTimeBest = viewModel.allTimeBestStreak()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Today's Goal
        KPICard(
            modifier = Modifier.weight(1f),
            label = "TODAY",
            value = "$todayPct%",
            subtitle = "$todayCount/$total done",
            accentColor = Color(0xFF6366F1)
        )
        // Top Streak
        KPICard(
            modifier = Modifier.weight(1f),
            label = "🔥 STREAK",
            value = "${topStreak}d",
            subtitle = if (topStreak > 0) topStreakName.take(15) else "Start logging!",
            accentColor = Color(0xFFF59E0B)
        )
        // Best Ever
        KPICard(
            modifier = Modifier.weight(1f),
            label = "🏆 BEST",
            value = "${allTimeBest}d",
            subtitle = "All-time record",
            accentColor = Color(0xFF10B981)
        )
    }
}

@Composable
private fun KPICard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selectedCategory: HabitCategory?,
    habitCounts: Map<HabitCategory, Int>,
    totalCount: Int,
    onSelect: (HabitCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelect(null) },
                label = { Text("All ($totalCount)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(14.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4F46E5),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1E293B),
                    labelColor = Color(0xFF94A3B8)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color(0xFF334155),
                    selectedBorderColor = Color(0xFF4F46E5),
                    enabled = true,
                    selected = selectedCategory == null
                )
            )
        }
        items(HabitCategory.entries.toList()) { cat ->
            val count = habitCounts[cat] ?: 0
            FilterChip(
                selected = selectedCategory == cat,
                onClick = { onSelect(cat) },
                label = {
                    Text(
                        "${cat.emoji} ${cat.label} ($count)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4F46E5),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1E293B),
                    labelColor = Color(0xFF94A3B8)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color(0xFF334155),
                    selectedBorderColor = Color(0xFF4F46E5),
                    enabled = true,
                    selected = selectedCategory == cat
                )
            )
        }
    }
}

@Composable
private fun WeekNavigator(
    weekDates: List<LocalDate>,
    weekOffset: Int,
    onShift: (Int) -> Unit,
    onReset: () -> Unit
) {
    val fmt = DateTimeFormatter.ofPattern("MMM d")
    val rangeText = "${weekDates.first().format(fmt)} – ${weekDates.last().format(fmt)}"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onShift(-1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronLeft, "Previous",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    rangeText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                IconButton(
                    onClick = { onShift(1) },
                    enabled = weekOffset < 0,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronRight, "Next",
                        tint = if (weekOffset < 0) Color(0xFF94A3B8) else Color(0xFF334155),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (weekOffset != 0) {
                TextButton(onClick = onReset) {
                    Text(
                        "Today",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF818CF8)
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    weekDates: List<LocalDate>,
    today: LocalDate,
    onToggle: (LocalDate) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = Color(habit.colorHex)
    val currentStreak = habit.currentStreak()
    val bestStreak = habit.bestStreak()
    val weekDone = weekDates.count { habit.isCompletedOn(it) }
    val weekPct = (weekDone * 100) / 7

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Habit info + actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Emoji icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(habit.emoji, fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            habit.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            habit.category.label,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Streak badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current streak
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("🔥", fontSize = 12.sp)
                            Text(
                                "${currentStreak}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentStreak > 0) Color(0xFFF59E0B) else Color(0xFF64748B)
                            )
                        }
                    }
                    // Best streak
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("🏆", fontSize = 12.sp)
                            Text(
                                "${bestStreak}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    // More options
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            "Edit",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 7-Day toggle row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDates.forEach { date ->
                    val isDone = habit.isCompletedOn(date)
                    val isToday = date == today
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val dayNum = date.dayOfMonth.toString()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            dayName.take(3).uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) Color(0xFF818CF8) else Color(0xFF64748B),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            dayNum,
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isToday) Color(0xFF818CF8) else Color(0xFF94A3B8)
                        )
                        // Toggle button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isDone) accentColor
                                    else Color(0xFF1E293B)
                                )
                                .border(
                                    1.dp,
                                    if (isDone) accentColor
                                    else if (isToday) Color(0xFF6366F1).copy(alpha = 0.5f)
                                    else Color(0xFF334155),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onToggle(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    Icons.Default.Check,
                                    "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Weekly progress bar
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1E293B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(weekPct / 100f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(accentColor, accentColor.copy(alpha = 0.7f))
                                )
                            )
                    )
                }
                Text(
                    "$weekDone/7 · $weekPct%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (weekPct == 100) Color(0xFF10B981) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun StreakLeaderboard(habits: List<Habit>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🔥 Streak Leaderboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Current streak vs personal best",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val sorted = habits.sortedByDescending { it.currentStreak() }
            val maxBest = maxOf(sorted.maxOfOrNull { it.bestStreak() } ?: 7, 7)

            sorted.forEach { habit ->
                val current = habit.currentStreak()
                val best = habit.bestStreak()
                val currentWidth = (current.toFloat() / maxBest).coerceIn(0f, 1f)
                val bestWidth = (best.toFloat() / maxBest).coerceIn(0f, 1f)

                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(habit.emoji, fontSize = 14.sp)
                            Text(
                                habit.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "🔥 ${current}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                "🏆 ${best}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Dual bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                    ) {
                        // Best streak background bar
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(bestWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.25f))
                        )
                        // Current streak foreground bar
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(currentWidth)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("📅", fontSize = 40.sp)
            Text(
                "No habits yet",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                "Add your first habit to start tracking\nyour daily progress and build streaks!",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                )
            ) {
                Icon(Icons.Default.Add, "Add", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Habit", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
