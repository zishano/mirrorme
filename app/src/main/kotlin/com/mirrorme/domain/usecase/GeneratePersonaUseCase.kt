package com.mirrorme.domain.usecase

import com.mirrorme.domain.model.AppCategory
import com.mirrorme.domain.model.Chronotype
import com.mirrorme.domain.model.DailyFeatures
import com.mirrorme.domain.model.FocusPattern
import com.mirrorme.domain.model.MoodState
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.model.SocialPattern
import com.mirrorme.domain.model.StressLevel
import com.mirrorme.domain.repository.BehaviorRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GeneratePersonaUseCase @Inject constructor(
    private val repository: BehaviorRepository,
    private val inferenceEngine: PersonaInferenceEngine
) {
    suspend operator fun invoke(): PersonaProfile {
        val recentDays = repository.getDailyFeatures(limit = 14).first()
        val today = recentDays.firstOrNull() ?: return PersonaProfile.empty()
        return inferenceEngine.infer(today, recentDays)
    }
}

// 纯规则推理引擎（Phase 1），Phase 2 替换为 TFLite 模型
class PersonaInferenceEngine @Inject constructor() {

    fun infer(today: DailyFeatures, history: List<DailyFeatures>): PersonaProfile {
        val chronotype = inferChronotype(today, history)
        val socialPattern = inferSocialPattern(today, history)
        val focusPattern = inferFocusPattern(today, history)
        val mood = inferMood(today)
        val stress = inferStress(today, history)
        val bigFive = inferBigFive(today, history, socialPattern, focusPattern)
        val insights = buildInsights(today, history, mood, stress)
        val suggestions = buildSuggestions(mood, stress, chronotype, focusPattern)

        return PersonaProfile(
            generatedAt = System.currentTimeMillis(),
            basedOnDays = history.size,
            openness = bigFive[0],
            conscientiousness = bigFive[1],
            extraversion = bigFive[2],
            agreeableness = bigFive[3],
            neuroticism = bigFive[4],
            todayMood = mood,
            moodScore = moodScore(today),
            stressLevel = stress,
            chronotype = chronotype,
            socialPattern = socialPattern,
            focusPattern = focusPattern,
            personaTitle = buildTitle(chronotype, socialPattern, focusPattern),
            personaSummary = buildSummary(today, chronotype, socialPattern, focusPattern, mood),
            insights = insights,
            suggestions = suggestions
        )
    }

    private fun inferChronotype(today: DailyFeatures, history: List<DailyFeatures>): Chronotype {
        val avgFirstUnlock = history.map { it.firstUnlockHour }.filter { it >= 0 }.average()
        val avgNightScreen = history.map { it.nightScreenMinutes }.average()
        return when {
            avgFirstUnlock < 7.0 -> Chronotype.EARLY_BIRD
            avgNightScreen > 45 || avgFirstUnlock > 9.0 -> Chronotype.NIGHT_OWL
            else -> Chronotype.INTERMEDIATE
        }
    }

    private fun inferSocialPattern(today: DailyFeatures, history: List<DailyFeatures>): SocialPattern {
        val avgSocial = history.map { it.socialAppRatio }.average()
        val avgCalls = history.map { it.callCount }.average()
        return when {
            avgSocial > 0.35 || avgCalls > 5 -> SocialPattern.HIGHLY_SOCIAL
            avgSocial < 0.10 && avgCalls < 2 -> SocialPattern.INTROVERT
            else -> SocialPattern.MODERATE
        }
    }

    private fun inferFocusPattern(today: DailyFeatures, history: List<DailyFeatures>): FocusPattern {
        val avgUnlocks = history.map { it.screenUnlockCount }.average()
        val avgApps = history.map { it.uniqueAppsUsed }.average()
        return when {
            avgUnlocks < 30 && avgApps < 8 -> FocusPattern.DEEP_FOCUS
            avgUnlocks > 80 || avgApps > 20 -> FocusPattern.SCATTERED
            else -> FocusPattern.BALANCED
        }
    }

    private fun inferMood(today: DailyFeatures): MoodState {
        val score = moodScore(today)
        return when {
            score > 0.5 -> MoodState.VERY_POSITIVE
            score > 0.1 -> MoodState.POSITIVE
            score > -0.1 -> MoodState.NEUTRAL
            score > -0.5 -> MoodState.NEGATIVE
            else -> MoodState.VERY_NEGATIVE
        }
    }

    // 简单规则：步数多/屏幕时间适中 → 积极；夜间使用多/解锁过高 → 消极
    private fun moodScore(f: DailyFeatures): Float {
        var score = 0f
        score += (f.stepCount / 10000f).coerceIn(0f, 0.3f)
        score -= (f.nightScreenMinutes / 120f).coerceIn(0f, 0.3f)
        score -= ((f.screenUnlockCount - 50) / 100f).coerceIn(0f, 0.2f)
        score += if (f.totalScreenOnMinutes in 60..240) 0.2f else 0f
        return score.coerceIn(-1f, 1f)
    }

    private fun inferStress(today: DailyFeatures, history: List<DailyFeatures>): StressLevel {
        val baseline = history.drop(1).map { it.screenUnlockCount }.average()
        val deviation = today.screenUnlockCount - baseline
        return when {
            deviation > 40 || today.nightScreenMinutes > 90 -> StressLevel.VERY_HIGH
            deviation > 20 || today.nightScreenMinutes > 60 -> StressLevel.HIGH
            deviation > 0 -> StressLevel.MEDIUM
            else -> StressLevel.LOW
        }
    }

    private fun inferBigFive(
        today: DailyFeatures,
        history: List<DailyFeatures>,
        social: SocialPattern,
        focus: FocusPattern
    ): FloatArray {
        // 基于行为特征的规则映射（Phase 2 替换为 TFLite 回归模型）
        val extraversion = when (social) {
            SocialPattern.HIGHLY_SOCIAL -> 0.75f
            SocialPattern.MODERATE -> 0.5f
            SocialPattern.INTROVERT -> 0.25f
        }
        val conscientiousness = when (focus) {
            FocusPattern.DEEP_FOCUS -> 0.75f
            FocusPattern.BALANCED -> 0.55f
            FocusPattern.SCATTERED -> 0.35f
        }
        val openness = (today.locationEntropy / 3.0).coerceIn(0.0, 1.0).toFloat()
            .let { (it * 0.6f + 0.2f) }
        val neuroticism = when {
            today.nightScreenMinutes > 60 || today.screenUnlockCount > 100 -> 0.7f
            today.screenUnlockCount < 30 -> 0.3f
            else -> 0.5f
        }
        val agreeableness = if (social == SocialPattern.INTROVERT) 0.55f else 0.6f
        return floatArrayOf(openness, conscientiousness, extraversion, agreeableness, neuroticism)
    }

    private fun buildInsights(
        today: DailyFeatures,
        history: List<DailyFeatures>,
        mood: MoodState,
        stress: StressLevel
    ): List<String> {
        val insights = mutableListOf<String>()
        if (today.nightScreenMinutes > 45)
            insights.add("今晚手机使用时间较长，可能影响睡眠质量")
        if (today.stepCount < 3000)
            insights.add("今天步数较少，久坐对健康不利")
        if (history.size >= 3 && today.screenUnlockCount > history.drop(1).map { it.screenUnlockCount }.average() * 1.4)
            insights.add("今日解锁次数明显高于你的平均水平，注意力可能较分散")
        if (today.topAppCategory == AppCategory.ENTERTAINMENT && today.entertainmentAppRatio > 0.5)
            insights.add("今天娱乐类App使用超过一半时间")
        if (stress == StressLevel.HIGH || stress == StressLevel.VERY_HIGH)
            insights.add("行为模式显示你今天可能有些压力")
        if (today.callCount > 8)
            insights.add("今天通话频繁，社交需求旺盛")
        return insights.ifEmpty { listOf("今天的行为模式较为规律，继续保持！") }
    }

    private fun buildSuggestions(
        mood: MoodState,
        stress: StressLevel,
        chronotype: Chronotype,
        focus: FocusPattern
    ): List<String> {
        val suggestions = mutableListOf<String>()
        if (stress == StressLevel.HIGH || stress == StressLevel.VERY_HIGH)
            suggestions.add("尝试睡前 30 分钟放下手机，进行冥想或轻度伸展")
        if (focus == FocusPattern.SCATTERED)
            suggestions.add("可以试试番茄工作法，25 分钟专注后再查看消息")
        if (chronotype == Chronotype.NIGHT_OWL)
            suggestions.add("尝试将睡前屏幕时间提前 1 小时，有助于改善睡眠节律")
        if (mood == MoodState.NEGATIVE || mood == MoodState.VERY_NEGATIVE)
            suggestions.add("外出走走，哪怕 10 分钟散步也能改善情绪")
        return suggestions.ifEmpty { listOf("保持当前的生活节奏，状态不错！") }
    }

    private fun buildTitle(c: Chronotype, s: SocialPattern, f: FocusPattern): String {
        return when {
            c == Chronotype.EARLY_BIRD && f == FocusPattern.DEEP_FOCUS -> "专注晨型人"
            c == Chronotype.NIGHT_OWL && s == SocialPattern.HIGHLY_SOCIAL -> "夜间社交达人"
            s == SocialPattern.INTROVERT && f == FocusPattern.DEEP_FOCUS -> "深度独处者"
            s == SocialPattern.HIGHLY_SOCIAL && f == FocusPattern.SCATTERED -> "活跃探索者"
            f == FocusPattern.DEEP_FOCUS -> "深度专注者"
            else -> "均衡生活者"
        }
    }

    private fun buildSummary(
        today: DailyFeatures,
        c: Chronotype,
        s: SocialPattern,
        f: FocusPattern,
        mood: MoodState
    ): String {
        val chronoDesc = when (c) {
            Chronotype.EARLY_BIRD -> "你是一位早起型人，精力集中在上午"
            Chronotype.NIGHT_OWL -> "你习惯晚睡，深夜是你的活跃时段"
            Chronotype.INTERMEDIATE -> "你的作息比较规律"
        }
        val socialDesc = when (s) {
            SocialPattern.HIGHLY_SOCIAL -> "，社交需求旺盛"
            SocialPattern.INTROVERT -> "，倾向于独处和深度思考"
            SocialPattern.MODERATE -> "，保持适度的社交互动"
        }
        val moodDesc = when (mood) {
            MoodState.VERY_POSITIVE, MoodState.POSITIVE -> "，今天状态积极"
            MoodState.NEGATIVE, MoodState.VERY_NEGATIVE -> "，今天情绪偏低落"
            MoodState.NEUTRAL -> ""
        }
        return "$chronoDesc$socialDesc$moodDesc。已连续追踪 ${today.date} 天。"
    }
}

fun PersonaProfile.Companion.empty() = PersonaProfile(
    generatedAt = System.currentTimeMillis(), basedOnDays = 0,
    openness = 0.5f, conscientiousness = 0.5f, extraversion = 0.5f,
    agreeableness = 0.5f, neuroticism = 0.5f,
    todayMood = MoodState.NEUTRAL, moodScore = 0f, stressLevel = StressLevel.LOW,
    chronotype = Chronotype.INTERMEDIATE, socialPattern = SocialPattern.MODERATE,
    focusPattern = FocusPattern.BALANCED,
    personaTitle = "数据采集中...", personaSummary = "继续使用以生成你的专属画像",
    insights = emptyList(), suggestions = emptyList()
)
