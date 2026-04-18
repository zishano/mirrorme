package com.mirrorme.ui.persona

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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirrorme.domain.model.Chronotype
import com.mirrorme.domain.model.FocusPattern
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.model.SocialPattern
import com.mirrorme.ui.theme.BackgroundDark
import com.mirrorme.ui.theme.CardDark
import com.mirrorme.ui.theme.Secondary
import com.mirrorme.ui.theme.SurfaceDark
import com.mirrorme.ui.theme.TextHint
import com.mirrorme.ui.theme.TextPrimary
import com.mirrorme.ui.theme.TextSecondary

@Composable
fun PersonaScreen(onBack: () -> Unit, vm: PersonaViewModel = hiltViewModel()) {
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
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Secondary)
                    }
                    Text("我的画像", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                }
            }

            item { PersonaHeroCard(persona) }
            item { HabitTagsCard(persona) }
            item { ChronotypCard(persona) }
        }
    }
}

@Composable
private fun PersonaHeroCard(persona: PersonaProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(persona.personaTitle,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = Secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(persona.personaSummary,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun HabitTagsCard(persona: PersonaProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("行为习惯标签", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(persona.chronotype.label())
                TagChip(persona.socialPattern.label())
                TagChip(persona.focusPattern.label())
            }
        }
    }
}

@Composable
private fun TagChip(label: String) {
    Box(
        modifier = Modifier
            .background(Secondary.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Secondary)
    }
}

@Composable
private fun ChronotypCard(persona: PersonaProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("更多分析维度", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            LabelValue("作息类型", persona.chronotype.description())
            LabelValue("社交模式", persona.socialPattern.description())
            LabelValue("专注模式", persona.focusPattern.description())
            LabelValue("数据天数", "${persona.basedOnDays} 天")
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextHint)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}

private fun Chronotype.label() = when (this) {
    Chronotype.EARLY_BIRD -> "早鸟型"
    Chronotype.INTERMEDIATE -> "规律型"
    Chronotype.NIGHT_OWL -> "夜猫子"
}

private fun Chronotype.description() = when (this) {
    Chronotype.EARLY_BIRD -> "早起，上午精力充沛"
    Chronotype.INTERMEDIATE -> "作息规律，精力均衡"
    Chronotype.NIGHT_OWL -> "晚睡，深夜最活跃"
}

private fun SocialPattern.label() = when (this) {
    SocialPattern.HIGHLY_SOCIAL -> "社交达人"
    SocialPattern.MODERATE -> "适度社交"
    SocialPattern.INTROVERT -> "独处者"
}

private fun SocialPattern.description() = when (this) {
    SocialPattern.HIGHLY_SOCIAL -> "频繁社交互动，活跃于社群"
    SocialPattern.MODERATE -> "保持适度的社交平衡"
    SocialPattern.INTROVERT -> "偏好独处与深度思考"
}

private fun FocusPattern.label() = when (this) {
    FocusPattern.DEEP_FOCUS -> "深度专注"
    FocusPattern.BALANCED -> "均衡切换"
    FocusPattern.SCATTERED -> "多任务型"
}

private fun FocusPattern.description() = when (this) {
    FocusPattern.DEEP_FOCUS -> "长时间专注单一任务"
    FocusPattern.BALANCED -> "能在专注与切换间平衡"
    FocusPattern.SCATTERED -> "频繁切换，多线程思维"
}
