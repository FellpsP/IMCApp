package com.example.imc.domain

object HealthCalculator {

    fun calculateBMI(weight: Double, height: Double): Double {
        if (weight <= 0.0 || height <= 0.0) return 0.0
        if (height < 0.5) return 0.0
        return weight / (height * height)
    }

    fun classifyBMI(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Abaixo do peso"
            bmi < 25.0 -> "Peso normal"
            bmi < 30.0 -> "Sobrepeso"
            bmi < 35.0 -> "Obesidade Grau I"
            bmi < 40.0 -> "Obesidade Grau II"
            else -> "Obesidade Grau III"
        }
    }

    fun calculateBMR(weight: Double, height: Double, age: Int, isMale: Boolean): Double {
        return if (isMale) {
            (10 * weight) + (6.25 * height) - (5 * age) + 5
        } else {
            (10 * weight) + (6.25 * height) - (5 * age) - 161
        }
    }

    fun calculateIdealWeight(height: Double, isMale: Boolean): Double {
        val heightInInches = height / 2.54
        val baseWeight = if (isMale) 50.0 else 45.5
        return if (heightInInches > 60) {
            baseWeight + 2.3 * (heightInInches - 60)
        } else {
            baseWeight
        }
    }

    fun calculateDailyCalorieNeeds(bmr: Double, activityLevel: Int): Double {
        val multiplier = when (activityLevel) {
            1 -> 1.2   // Sedentário
            2 -> 1.375 // Leve
            3 -> 1.55  // Moderado
            4 -> 1.725 // Intenso
            else -> 1.2
        }
        return bmr * multiplier
    }
}