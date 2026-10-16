package com.gulimovbunyod.flow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gulimovbunyod.flow.ui.theme.FlowTheme

private val Orange = Color(0xFFFF7849)
private val Panel = Color(0xFF202127)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlowTheme {
                FlowHome()
            }
        }
    }
}

@Composable
private fun FlowHome() {
    val habits = listOf(
        "🌙" to ("Uyqu" to "Vaqtida dam olish"),
        "🌿" to ("Havoda 30 daqiqa" to "Sayr va toza havo"),
        "💻" to ("Kodlash 30 daqiqa" to "Bugun o‘z ustingda ishlash")
    )
    var completed by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val progress = completed.size / 3f

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("FLOW", color = Orange, style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black)
                    Text("Ritmingni ushla.", style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                }
                Surface(shape = RoundedCornerShape(18.dp), color = Panel) {
                    Text("🔥  3 kun", modifier = Modifier.padding(14.dp),
                        fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Panel
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("BUGUNGI NATIJA", color = Color.LightGray,
                        style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${completed.size * 100 / 3}%",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black, color = Orange)
                        Spacer(Modifier.width(10.dp))
                        Text("${completed.size}/3 odat bajarildi",
                            modifier = Modifier.padding(bottom = 9.dp),
                            color = Color.LightGray)
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(9.dp),
                        color = Orange,
                        trackColor = Color.DarkGray
                    )
                    Text(
                        if (completed.size == 3) "Ajoyib! Bugungi reja bajarildi 🎉"
                        else "Kichik qadamlar katta natija beradi.",
                        color = Color.White
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Bugungi odatlar", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Text("Har bir belgi — oldinga qadam.", color = Color.Gray)
            }

            habits.forEachIndexed { index, item ->
                val done = index in completed
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = if (done) Color(0xFF183B30) else Panel,
                    onClick = {
                        completed = if (done) completed - index else completed + index
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(shape = RoundedCornerShape(15.dp),
                            color = if (done) Color(0xFF245843) else Color(0xFF303139)) {
                            Text(item.first, modifier = Modifier.padding(13.dp),
                                style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.second.first, fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(item.second.second, color = Color.LightGray,
                                style = MaterialTheme.typography.bodySmall)
                        }
                        Checkbox(
                            checked = done,
                            onCheckedChange = {
                                completed = if (it) completed + index else completed - index
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF42C58A)
                            )
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF30271F),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Davomiylik muhim", fontWeight = FontWeight.Bold)
                        Text("Mukammallik emas, muntazamlik.",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text(
                "FLOW · DEMO 0.1.0",
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
