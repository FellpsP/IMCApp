package com.example.imc.data.model

data class HealthMetrics(
    val bmi: Double,
    val bmiClassification: String,
    val bmr: Double? = null,
    val bodyFatPercentage: Double? = null,
    val idealWeight: Double? = null,
    val dailyCalorieNeeds: Double? = null
)