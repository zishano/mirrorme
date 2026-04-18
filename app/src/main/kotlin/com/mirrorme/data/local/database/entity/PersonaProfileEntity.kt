package com.mirrorme.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mirrorme.domain.model.Chronotype
import com.mirrorme.domain.model.FocusPattern
import com.mirrorme.domain.model.MoodState
import com.mirrorme.domain.model.PersonaProfile
import com.mirrorme.domain.model.SocialPattern
import com.mirrorme.domain.model.StressLevel

@Entity(tableName = "persona_profiles")
data class PersonaProfileEntity(
    @PrimaryKey val generatedAt: Long,
    val basedOnDays: Int,
    val openness: Float,
    val conscientiousness: Float,
    val extraversion: Float,
    val agreeableness: Float,
    val neuroticism: Float,
    val todayMood: String,
    val moodScore: Float,
    val stressLevel: String,
    val chronotype: String,
    val socialPattern: String,
    val focusPattern: String,
    val personaTitle: String,
    val personaSummary: String,
    val insightsJson: String,   // JSON array
    val suggestionsJson: String // JSON array
) {
    fun toDomain() = PersonaProfile(
        generatedAt = generatedAt,
        basedOnDays = basedOnDays,
        openness = openness,
        conscientiousness = conscientiousness,
        extraversion = extraversion,
        agreeableness = agreeableness,
        neuroticism = neuroticism,
        todayMood = MoodState.valueOf(todayMood),
        moodScore = moodScore,
        stressLevel = StressLevel.valueOf(stressLevel),
        chronotype = Chronotype.valueOf(chronotype),
        socialPattern = SocialPattern.valueOf(socialPattern),
        focusPattern = FocusPattern.valueOf(focusPattern),
        personaTitle = personaTitle,
        personaSummary = personaSummary,
        insights = insightsJson.parseJsonArray(),
        suggestions = suggestionsJson.parseJsonArray()
    )

    companion object {
        fun fromDomain(p: PersonaProfile) = PersonaProfileEntity(
            generatedAt = p.generatedAt,
            basedOnDays = p.basedOnDays,
            openness = p.openness,
            conscientiousness = p.conscientiousness,
            extraversion = p.extraversion,
            agreeableness = p.agreeableness,
            neuroticism = p.neuroticism,
            todayMood = p.todayMood.name,
            moodScore = p.moodScore,
            stressLevel = p.stressLevel.name,
            chronotype = p.chronotype.name,
            socialPattern = p.socialPattern.name,
            focusPattern = p.focusPattern.name,
            personaTitle = p.personaTitle,
            personaSummary = p.personaSummary,
            insightsJson = p.insights.toJsonArray(),
            suggestionsJson = p.suggestions.toJsonArray()
        )
    }
}

private fun List<String>.toJsonArray() = "[${joinToString(",") { "\"$it\"" }}]"
private fun String.parseJsonArray() = removePrefix("[").removeSuffix("]")
    .split(",")
    .map { it.trim().removeSurrounding("\"") }
    .filter { it.isNotEmpty() }
