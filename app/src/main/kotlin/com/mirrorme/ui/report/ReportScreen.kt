package com.mirrorme.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.ui.theme.BackgroundDark
import com.mirrorme.ui.theme.CardDark
import com.mirrorme.ui.theme.Secondary
import com.mirrorme.ui.theme.SurfaceDark
import com.mirrorme.ui.theme.TextHint
import com.mirrorme.ui.theme.TextPrimary
import com.mirrorme.ui.theme.TextSecondary

@Composable
fun ReportScreen(onBack: () -> Unit, vm: ReportViewModel = hiltViewModel()) {
    val features by vm.recentFeatures.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Secondary)
                    }
                    Text("行为报告", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                }
            }

            item { WeeklySummaryCard(features) }

            features.forEach { day ->
                item { DayCard(day) }
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(features: List<DailyFeatures>) {
    if (features.isEmpty()) return
    val avgUnlocks = features.map { it.screenUnlockCount }.average().toInt()
    val avgSteps = features.map { it.stepCount }.average().toInt()
    val avgScreen = features.map { it.totalScreenOnMinutes }.average().toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("近期均值（${features.size}天）",
                style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            StatRow("平均解锁次数", "$avgUnlocks 次")
            StatRow("平均步数", "$avgSteps 步")
            StatRow("平均屏幕时间", "${avgScreen} 分钟")
        }
    }
}

@Composable
private fun DayCard(day: DailyFeatures) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                java.text.SimpleDateFormat("MM月dd日", java.util.Locale.CHINA).format(java.util.Date(day.date)),
                style = MaterialTheme.typography.titleLarge,
                color = Secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatRow("解锁", "${day.screenUnlockCount} 次")
            StatRow("屏幕时间", "${day.totalScreenOnMinutes} 分钟")
            StatRow("步数", "${day.stepCount} 步")
            StatRow("通话", "${day.callCount} 次 · ${day.totalCallMinutes} 分钟")
            StatRow("主要App类型", day.topAppCategory.name)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextHint)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}
