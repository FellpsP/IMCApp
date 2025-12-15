package com.example.imc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bmi_records")
data class BmiData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weight: Double,
    val height: Double,
    val bmi: Double,
    val classification: String,
    val timestamp: Long = System.currentTimeMillis(),

    // Dados adicionais (use valores padrão em vez de null)
    val bmr: Double = 0.0,
    val idealWeight: Double = 0.0,
    val dailyCalorieNeeds: Double = 0.0,
    val age: Int = 0,
    val isMale: Boolean = true,
    val activityLevel: Int = 1
) {
    fun getFormattedDate(): String {
        return Date(timestamp).toString()
    }

    // Função auxiliar para verificar se tem dados completos
    fun hasCompleteData(): Boolean {
        return age > 0 && bmr > 0
    }
}