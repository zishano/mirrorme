package com.mirrorme.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorme.domain.model.MoodState
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.model.StressLevel
import com.mirrorme.ui.theme.BackgroundDark
import com.mirrorme.ui.theme.CardDark
import com.mirrorme.ui.theme.MoodNegative
import com.mirrorme.ui.theme.MoodNeutral
import com.mirrorme.ui.theme.MoodPositive
import com.mirrorme.ui.theme.MoodVeryNegative
import com.mirrorme.ui.theme.MoodVeryPositive
import com.mirrorme.ui.theme.PrimaryVariant
import com.mirrorme.ui.theme.Secondary
import com.mirrorme.ui.theme.SurfaceDark
import com.mirrorme.ui.theme.TextHint
import com.mirrorme.ui.theme.TextPrimary
import com.mirrorme.ui.theme.TextSecondary
import com.mirrorme.ui.theme.TraitAgreeableness
import com.mirrorme.ui.theme.TraitConscientiousness
import com.mirrorme.ui.theme.TraitExtraversion
import com.mirrorme.ui.theme.TraitNeuroticism
import com.mirrorme.ui.theme.TraitOpenness

@Composable
fun HomeScreen(
    onViewPersona: () -> Unit,
    onViewReport: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val persona by vm.persona.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { TopBar(onRefresh = vm::refreshPersona) }
            item { PersonaTitleCard(persona = persona, onClick = onViewPersona) }
            item { MoodStressRow(persona = persona) }
            item { BigFiveCard(persona = persona) }
            item { InsightsCard(insights = persona.insights) }
            item { SuggestionsCard(suggestions = persona.suggestions) }
            item { QuickStatsRow(persona = persona, onViewReport = onViewReport) }
        }
    }
}

@Composable
private fun TopBar(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("自镜", style = MaterialTheme.typography.displayMedium, color = TextPrimary)
            Text("MirrorMe", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        IconButton(onClick = onRefresh) {
            Icon(Icons.Default.Refresh, contentDescription = "刷新", tint = Secondary)
        }
    }
}

@Composable
private fun PersonaTitleCard(persona: PersonaProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(PrimaryVariant.copy(alpha = 0.8f), Secondary.copy(alpha = 0.6f)))
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            persona.personaTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "基于 ${persona.basedOnDays} 天数据",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    persona.personaSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun MoodStressRow(persona: PersonaProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MiniCard(
            modifier = Modifier.weight(1f),
            title = "今日情绪",
            value = persona.todayMood.label(),
            color = persona.todayMood.color()
        )
        MiniCard(
            modifier = Modifier.weight(1f),
            title = "压力水平",
            value = persona.stressLevel.label(),
            color = persona.stressLevel.color()
        )
    }
}

@Composable
private fun MiniCard(modifier: Modifier = Modifier, title: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextHint)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun BigFiveCard(persona: PersonaProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("人格维度", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            TraitBar("开放性", persona.openness, TraitOpenness)
            TraitBar("尽责性", persona.conscientiousness, TraitConscientiousness)
            TraitBar("外向性", persona.extraversion, TraitExtraversion)
            TraitBar("宜人性", persona.agreeableness, TraitAgreeableness)
            TraitBar("情绪稳定", 1f - persona.neuroticism, TraitNeuroticism)
        }
    }
}

@Composable
private fun TraitBar(label: String, value: Float, color: Color) {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "trait_$label"
    )
    LaunchedEffect(value) { progress = value }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(64.dp),
            style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = CardDark,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
private fun InsightsCard(insights: List<String>) {
    if (insights.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("今日洞察", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            insights.forEach { insight ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("·", color = Secondary, modifier = Modifier.width(16.dp))
                    Text(insight, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun SuggestionsCard(suggestions: List<String>) {
    if (suggestions.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("行动建议", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            suggestions.forEachIndexed { i, s ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${i + 1}.", color = PrimaryVariant, modifier = Modifier.width(24.dp))
                    Text(s, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(persona: PersonaProfile, onViewReport: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewReport),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = Secondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("查看详细报告 →", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}

private fun MoodState.label() = when (this) {
    MoodState.VERY_POSITIVE -> "很好"
    MoodState.POSITIVE -> "积极"
    MoodState.NEUTRAL -> "平静"
    MoodState.NEGATIVE -> "低落"
    MoodState.VERY_NEGATIVE -> "很差"
}

private fun MoodState.color() = when (this) {
    MoodState.VERY_POSITIVE -> MoodVeryPositive
    MoodState.POSITIVE -> MoodPositive
    MoodState.NEUTRAL -> MoodNeutral
    MoodState.NEGATIVE -> MoodNegative
    MoodState.VERY_NEGATIVE -> MoodVeryNegative
}

private fun StressLevel.label() = when (this) {
    StressLevel.LOW -> "低"
    StressLevel.MEDIUM -> "中"
    StressLevel.HIGH -> "高"
    StressLevel.VERY_HIGH -> "很高"
}

private fun StressLevel.color() = when (this) {
    StressLevel.LOW -> MoodVeryPositive
    StressLevel.MEDIUM -> MoodNeutral
    StressLevel.HIGH -> MoodNegative
    StressLevel.VERY_HIGH -> MoodVeryNegative
}
