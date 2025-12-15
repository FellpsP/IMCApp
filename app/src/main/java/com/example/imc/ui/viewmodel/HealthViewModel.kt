package com.example.imc.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imc.data.AppDatabase
import com.example.imc.data.model.BmiData
import com.example.imc.domain.HealthCalculator
import com.example.imc.data.model.HealthMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    private fun validateWeight(weight: String): String? {
        return when {
            weight.isBlank() -> "Peso é obrigatório"
            !weight.matches(Regex("^\\d+([.,]\\d+)?$")) -> "Use apenas números (ex: 70.5)"
            else -> {
                val weightValue = weight.replace(',', '.').toDoubleOrNull()
                when {
                    weightValue == null -> "Valor inválido"
                    weightValue <= 0 -> "Peso deve ser maior que zero"
                    weightValue < 20 -> "Peso mínimo: 20 kg"
                    weightValue > 300 -> "Peso máximo: 300 kg"
                    else -> null // Válido
                }
            }
        }
    }

    private fun validateHeight(height: String): String? {
        return when {
            height.isBlank() -> "Altura é obrigatória"
            !height.matches(Regex("^\\d+([.,]\\d+)?$")) -> "Use apenas números (ex: 175)"
            else -> {
                val heightValue = height.replace(',', '.').toDoubleOrNull()
                when {
                    heightValue == null -> "Valor inválido"
                    heightValue <= 0 -> "Altura deve ser maior que zero"
                    heightValue < 50 -> "Altura mínima: 50 cm"
                    heightValue > 250 -> "Altura máxima: 250 cm"
                    else -> null // Válido
                }
            }
        }
    }

    private fun validateAge(age: String): String? {
        return when {
            age.isBlank() -> null // Opcional
            !age.matches(Regex("^\\d+$")) -> "Idade deve ser um número inteiro"
            else -> {
                val ageValue = age.toIntOrNull()
                when {
                    ageValue == null -> "Valor inválido"
                    ageValue <= 0 -> "Idade deve ser maior que zero"
                    ageValue > 120 -> "Idade máxima: 120 anos"
                    else -> null // Válido
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    fun updateWeight(weight: String) {
        // Valida o peso
        val weightError = validateWeight(weight)

        // Atualiza o estado
        _uiState.value = _uiState.value.copy(
            weight = weight,
            weightError = weightError,
            isFormValid = isFormValid(
                weightError = weightError,
                heightError = _uiState.value.heightError,
                ageError = _uiState.value.ageError,
                weight = weight,
                height = _uiState.value.height
            )
        )

        // Só calcula se o formulário for válido
        if (_uiState.value.isFormValid) {
            calculateHealthMetrics()
        } else {
            // Limpa os resultados se o formulário estiver inválido
            _uiState.value = _uiState.value.copy(metrics = null)
        }
    }

    fun updateHeight(height: String) {
        val heightError = validateHeight(height)

        _uiState.value = _uiState.value.copy(
            height = height,
            heightError = heightError,
            isFormValid = isFormValid(
                weightError = _uiState.value.weightError,
                heightError = heightError,
                ageError = _uiState.value.ageError,
                weight = _uiState.value.weight,
                height = height
            )
        )

        if (_uiState.value.isFormValid) {
            calculateHealthMetrics()
        } else {
            _uiState.value = _uiState.value.copy(metrics = null)
        }
    }

    fun updateAge(age: String) {
        val ageError = validateAge(age)

        _uiState.value = _uiState.value.copy(
            age = age,
            ageError = ageError,
            isFormValid = isFormValid(
                weightError = _uiState.value.weightError,
                heightError = _uiState.value.heightError,
                ageError = ageError,
                weight = _uiState.value.weight,
                height = _uiState.value.height
            )
        )

        if (_uiState.value.isFormValid) {
            calculateHealthMetrics()
        }
    }

    fun updateGender(isMale: Boolean) {
        _uiState.value = _uiState.value.copy(isMale = isMale)
        calculateHealthMetrics()
    }

    fun updateActivityLevel(level: Int) {
        _uiState.value = _uiState.value.copy(activityLevel = level)
        calculateHealthMetrics()
    }

    private fun calculateHealthMetrics() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val weight = currentState.weight.toDoubleOrNull() ?: 0.0
                val heightCm = currentState.height.toDoubleOrNull() ?: 0.0
                val age = currentState.age.toIntOrNull() ?: 0

                val heightM = heightCm / 100

                val bmi = HealthCalculator.calculateBMI(weight, heightM)
                val classification = HealthCalculator.classifyBMI(bmi)

                val bmr = if (weight > 0 && heightCm > 0 && age > 0) {
                    HealthCalculator.calculateBMR(weight, heightCm, age, currentState.isMale)
                } else null

                val idealWeight = if (heightCm > 0) {
                    HealthCalculator.calculateIdealWeight(heightCm, currentState.isMale)
                } else null

                val dailyCalorieNeeds = if (bmr != null) {
                    HealthCalculator.calculateDailyCalorieNeeds(bmr, currentState.activityLevel)
                } else null

                _uiState.value = currentState.copy(
                    metrics = HealthMetrics(
                        bmi = bmi,
                        bmiClassification = classification,
                        bmr = bmr,
                        idealWeight = idealWeight,
                        dailyCalorieNeeds = dailyCalorieNeeds
                    ),
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro no cálculo: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    fun saveCurrentRecord(context: Context) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value

                if (!currentState.isFormValid) {
                    _uiState.value = currentState.copy(
                        error = "❌ Corrija os erros antes de salvar"
                    )
                    return@launch
                }

                // Validação básica
                val weight = currentState.weight.toDoubleOrNull() ?: 0.0
                val heightCm = currentState.height.toDoubleOrNull() ?: 0.0
                val age = currentState.age.toIntOrNull() ?: 0

                if (weight <= 0 || heightCm <= 0) {
                    _uiState.value = currentState.copy(
                        error = "Peso e altura devem ser maiores que zero"
                    )
                    return@launch  // Sai da função
                }

                // Recalcula para garantir consistência
                val heightM = heightCm / 100
                val bmi = HealthCalculator.calculateBMI(weight, heightM)
                val classification = HealthCalculator.classifyBMI(bmi)

                val bmr = if (age > 0) {
                    HealthCalculator.calculateBMR(weight, heightCm, age, currentState.isMale)
                } else 0.0

                val idealWeight = HealthCalculator.calculateIdealWeight(heightCm, currentState.isMale)

                val dailyCalorieNeeds = if (bmr > 0) {
                    HealthCalculator.calculateDailyCalorieNeeds(bmr, currentState.activityLevel)
                } else 0.0

                // Cria objeto BmiData
                val record = BmiData(
                    weight = weight,
                    height = heightCm,
                    bmi = bmi,
                    classification = classification,
                    bmr = bmr,
                    idealWeight = idealWeight,
                    dailyCalorieNeeds = dailyCalorieNeeds,
                    age = age,
                    isMale = currentState.isMale,
                    activityLevel = currentState.activityLevel
                )

                // Acessa banco de dados
                val database = AppDatabase.getDatabase(context)
                val id = database.bmiDao().insert(record)

                // Atualiza estado com mensagem
                _uiState.value = currentState.copy(
                    error = "✅ Salvo com sucesso!",
                    metrics = HealthMetrics(
                        bmi = bmi,
                        bmiClassification = classification,
                        bmr = bmr,
                        idealWeight = idealWeight,
                        dailyCalorieNeeds = dailyCalorieNeeds
                    )
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "❌ Erro ao salvar: ${e.message}"
                )
            }
        }
    }
}



data class HealthUiState(
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val isMale: Boolean = true,
    val activityLevel: Int = 1,
    val metrics: HealthMetrics? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    val weightError: String? = null,
    val heightError: String? = null,
    val ageError: String? = null,

    val isFormValid: Boolean = false
)

private fun isFormValid(
    weightError: String?,
    heightError: String?,
    ageError: String?,
    weight: String,
    height: String
): Boolean {
    return weightError == null &&
            heightError == null &&
            ageError == null &&
            weight.isNotBlank() &&
            height.isNotBlank()
}

