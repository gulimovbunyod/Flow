package com.gulimovbunyod.flow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gulimovbunyod.flow.ui.theme.FlowTheme
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Orange = Color(0xFFFF8744)
private val Panel = Color(0xFF191D24)
private val Muted = Color(0xFF9AA3AF)
private val NotDone = Color(0xFF555B65)
private val Partial = Color(0xFFFFC247)
private val Complete = Color(0xFF43D49A)
private val FlameBlue = Color(0xFF42A5F5)
private val FlameGray = Color(0xFF8B929C)
private val FlameBlack = Color(0xFF272B32)

private data class Habit(val emoji: String, val name: String, val description: String)
private val habits = listOf(
    Habit("🌙", "Uyqu", "Vaqtida dam olish"),
    Habit("🌿", "Havoda 30 daqiqa", "Sayr va toza havo"),
    Habit("💻", "Kodlash 30 daqiqa", "Kod yozishga vaqt ajratish")
)

private enum class Page { TODAY, YESTERDAY, HISTORY }
private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("uz"))
private fun dateLabel(date: LocalDate): String = date.format(dateFormatter)
private fun dayStatuses(records: Map<String, List<Int>>, date: LocalDate): List<Int> =
    records[date.toString()] ?: List(habits.size) { 0 }
private fun isActive(records: Map<String, List<Int>>, date: LocalDate): Boolean =
    dayStatuses(records, date).any { it > 0 }

private fun currentStreak(records: Map<String, List<Int>>, today: LocalDate): Int {
    var cursor = when {
        isActive(records, today) -> today
        isActive(records, today.minusDays(1)) -> today.minusDays(1)
        else -> return 0
    }
    var count = 0
    while (isActive(records, cursor)) {
        count++
        cursor = cursor.minusDays(1)
    }
    return count
}

private fun loadRecords(context: Context): Map<String, List<Int>> {
    val raw = context.getSharedPreferences("flow_habits", Context.MODE_PRIVATE)
        .getString("records", "{}") ?: "{}"
    return try {
        val root = JSONObject(raw)
        buildMap {
            val keys = root.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val array = root.optJSONArray(key) ?: continue
                if (array.length() == habits.size) {
                    put(key, List(habits.size) { i -> array.optInt(i, 0).coerceIn(0, 2) })
                }
            }
        }
    } catch (_: Exception) {
        emptyMap()
    }
}

private fun saveRecords(context: Context, records: Map<String, List<Int>>) {
    val root = JSONObject()
    records.forEach { (date, statuses) ->
        root.put(date, JSONArray(statuses))
    }
    context.getSharedPreferences("flow_habits", Context.MODE_PRIVATE)
        .edit().putString("records", root.toString()).apply()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FlowTheme { FlowHome(this) } }
    }
}

@Composable
private fun FlowHome(context: Context) {
    val today = LocalDate.now()
    var records by remember { mutableStateOf(loadRecords(context)) }
    var page by remember { mutableStateOf(Page.TODAY) }
    var selectedHistoryDate by remember { mutableStateOf(today) }

    val activeDate = when (page) {
        Page.TODAY -> today
        Page.YESTERDAY -> today.minusDays(1)
        Page.HISTORY -> selectedHistoryDate
    }
    val statuses = dayStatuses(records, activeDate)
    val streak = currentStreak(records, today)
    val todayActive = isActive(records, today)
    val yesterdayActive = isActive(records, today.minusDays(1))
    val flameColor = when {
        todayActive && yesterdayActive -> Orange
        todayActive -> FlameBlue
        yesterdayActive -> FlameGray
        else -> FlameBlack
    }

    fun updateStatus(index: Int, status: Int) {
        val updated = records.toMutableMap()
        val day = dayStatuses(records, activeDate).toMutableList()
        day[index] = status
        if (day.all { it == 0 }) updated.remove(activeDate.toString())
        else updated[activeDate.toString()] = day
        records = updated.toMap()
        saveRecords(context, records)
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { insets ->
        Column(
            modifier = Modifier.fillMaxSize().padding(insets).verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("FLOW", color = Orange, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("Ritmingni ushla.", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                }
                FlameIcon(color = flameColor, animate = todayActive && yesterdayActive,
                    modifier = Modifier.size(48.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Panel).padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Page.entries.forEach { item ->
                    val selected = page == item
                    val label = when (item) {
                        Page.TODAY -> "Bugun"
                        Page.YESTERDAY -> "Kecha"
                        Page.HISTORY -> "Tarix"
                    }
                    Box(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .background(if (selected) Color(0xFF343943) else Color.Transparent)
                            .clickable { page = item }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (selected) Color.White else Muted,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }

            if (page != Page.HISTORY) {
                Text(if (page == Page.TODAY) dateLabel(today) else dateLabel(today.minusDays(1)),
                    color = Muted, style = MaterialTheme.typography.bodyMedium)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = Panel
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    FlameIcon(color = flameColor, animate = todayActive && yesterdayActive,
                        modifier = Modifier.size(54.dp))
                    Column(Modifier.weight(1f)) {
                        Text("$streak kun", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall)
                        Text("Ketma-ket faollik", color = Muted,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            if (page == Page.HISTORY) {
                HistoryCalendar(
                    today = today,
                    records = records,
                    selectedDate = selectedHistoryDate,
                    onSelect = { selectedHistoryDate = it }
                )
                Text(dateLabel(selectedHistoryDate), style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
            } else {
                Text(if (page == Page.TODAY) "Bugungi odatlar" else "Kecha",
                    style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            habits.forEachIndexed { index, habit ->
                val status = statuses[index]
                Surface(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Panel
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp))
                                .background(statusColor(status).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center) {
                                Text(habit.emoji, fontSize = 25.sp)
                            }
                            Column(Modifier.weight(1f)) {
                                Text(habit.name, fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium)
                                Text(habit.description, color = Muted,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            Box(Modifier.size(10.dp).clip(CircleShape).background(statusColor(status)))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()) {
                            StatusButton("Bajarilmadi", 0, status, NotDone, Modifier.weight(1f)) {
                                updateStatus(index, it)
                            }
                            StatusButton("Minimal", 1, status, Partial, Modifier.weight(1f)) {
                                updateStatus(index, it)
                            }
                            StatusButton("To‘liq", 2, status, Complete, Modifier.weight(1f)) {
                                updateStatus(index, it)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatusButton(
    label: String, value: Int, selected: Int, tint: Color, modifier: Modifier,
    onSelect: (Int) -> Unit
) {
    val isSelected = selected == value
    Surface(
        modifier = modifier.height(40.dp).clickable { onSelect(value) },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) tint.copy(alpha = 0.2f) else Color(0xFF252A32),
        contentColor = if (isSelected) tint else Muted
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
            Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
private fun HistoryCalendar(
    today: LocalDate,
    records: Map<String, List<Int>>,
    selectedDate: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    val dates = (29 downTo 0).map { today.minusDays(it.toLong()) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Oxirgi 30 kun", color = Muted, style = MaterialTheme.typography.bodySmall)
        dates.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                week.forEach { date ->
                    val selected = date == selectedDate
                    val active = isActive(records, date)
                    val color = when {
                        selected -> Orange
                        active -> Complete.copy(alpha = 0.25f)
                        else -> Panel
                    }
                    Surface(
                        modifier = Modifier.weight(1f).height(42.dp).clickable { onSelect(date) },
                        shape = RoundedCornerShape(11.dp), color = color
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(date.dayOfMonth.toString(),
                                color = if (selected) Color(0xFF17191D) else Color.White,
                                fontWeight = if (selected || active) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp)
                        }
                    }
                }
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private fun statusColor(status: Int): Color = when (status) {
    1 -> Partial
    2 -> Complete
    else -> NotDone
}

@Composable
private fun FlameIcon(color: Color, animate: Boolean, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "flame")
    val pulse by transition.animateFloat(
        initialValue = 0.94f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(650), repeatMode = RepeatMode.Reverse),
        label = "flame-pulse"
    )
    Canvas(modifier = modifier) {
        val factor = if (animate) pulse else 1f
        withTransform({ scale(factor, factor, pivot = center) }) {
            drawFlame(color)
        }
    }
}

private fun DrawScope.drawFlame(color: Color) {
    val w = size.width
    val h = size.height
    val outer = Path().apply {
        moveTo(w * 0.52f, h * 0.04f)
        cubicTo(w * 0.57f, h * 0.25f, w * 0.83f, h * 0.30f, w * 0.86f, h * 0.57f)
        cubicTo(w * 0.91f, h * 0.82f, w * 0.74f, h * 0.97f, w * 0.50f, h * 0.97f)
        cubicTo(w * 0.22f, h * 0.97f, w * 0.08f, h * 0.77f, w * 0.16f, h * 0.56f)
        cubicTo(w * 0.20f, h * 0.44f, w * 0.31f, h * 0.38f, w * 0.33f, h * 0.22f)
        cubicTo(w * 0.42f, h * 0.34f, w * 0.43f, h * 0.42f, w * 0.48f, h * 0.48f)
        cubicTo(w * 0.56f, h * 0.34f, w * 0.48f, h * 0.20f, w * 0.52f, h * 0.04f)
        close()
    }
    drawPath(outer, color)
    val inner = Path().apply {
        moveTo(w * 0.52f, h * 0.55f)
        cubicTo(w * 0.62f, h * 0.66f, w * 0.69f, h * 0.76f, w * 0.60f, h * 0.87f)
        cubicTo(w * 0.53f, h * 0.94f, w * 0.36f, h * 0.88f, w * 0.37f, h * 0.76f)
        cubicTo(w * 0.38f, h * 0.67f, w * 0.47f, h * 0.64f, w * 0.52f, h * 0.55f)
        close()
    }
    drawPath(inner, if (color == Orange) Color(0xFFFFD166) else color.copy(alpha = 0.55f))
}
