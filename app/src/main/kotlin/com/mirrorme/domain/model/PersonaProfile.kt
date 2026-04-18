package com.mirrorme.domain.model

data class PersonaProfile(
    val generatedAt: Long,
    val basedOnDays: Int,

    // Big Five 人格分数 (0.0 ~ 1.0)
    val openness: Float,           // 开放性
    val conscientiousness: Float,  // 尽责性
    val extraversion: Float,       // 外向性
    val agreeableness: Float,      // 宜人性
    val neuroticism: Float,        // 神经质（情绪稳定性反转）

    // 今日情绪
    val todayMood: MoodState,
    val moodScore: Float,          // -1.0 (极差) ~ 1.0 (极好)
    val stressLevel: StressLevel,

    // 行为习惯标签
    val chronotype: Chronotype,    // 早鸟/夜猫子/中间型
    val socialPattern: SocialPattern,
    val focusPattern: FocusPattern,

    // 人物画像文字描述（由规则引擎生成）
    val personaTitle: String,
    val personaSummary: String,
    val insights: List<String>,    // 今日洞察列表
    val suggestions: List<String>  // 行动建议列表
)

enum class MoodState {
    VERY_POSITIVE, POSITIVE, NEUTRAL, NEGATIVE, VERY_NEGATIVE
}

enum class StressLevel {
    LOW, MEDIUM, HIGH, VERY_HIGH
}

enum class Chronotype {
    EARLY_BIRD,   // 早鸟：首次解锁 < 7:00
    INTERMEDIATE, // 中间型
    NIGHT_OWL     // 夜猫子：夜间使用 > 60 min
}

enum class SocialPattern {
    HIGHLY_SOCIAL,  // 社交App > 40% + 通话频繁
    MODERATE,
    INTROVERT       // 社交App < 10% + 通话少
}

enum class FocusPattern {
    DEEP_FOCUS,   // 长时间单App使用，低解锁频次
    SCATTERED,    // 高频切换App，高解锁频次
    BALANCED
}
