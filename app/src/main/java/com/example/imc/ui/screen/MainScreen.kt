package com.example.imc.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imc.ui.viewmodel.HealthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HealthViewModel = viewModel(),
    onNavigateToHistory: () -> Unit = {}
) {

    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calculadora de Saúde",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Histórico",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ver Histórico")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.weight,
            onValueChange = viewModel::updateWeight,
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.weightError != null,
            supportingText = {
                if (uiState.weightError != null) {
                    Text(
                        text = uiState.weightError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text("Ex: 70.5")
                }
            },
            trailingIcon = {  // NOVO: Ícone indicativo
                if (uiState.weightError != null) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.height,
            onValueChange = viewModel::updateHeight,
            label = { Text("Altura (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.heightError != null,
            supportingText = {
                if (uiState.heightError != null) {
                    Text(
                        text = uiState.heightError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text("Ex: 175")
                }
            },
            trailingIcon = {
                if (uiState.heightError != null) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.age,
            onValueChange = viewModel::updateAge,
            label = { Text("Idade (opcional)") },  // NOVO: Indicar que é opcional
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.ageError != null,
            supportingText = {
                if (uiState.ageError != null) {
                    Text(
                        text = uiState.ageError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text("Necessário para TMB e calorias")
                }
            },
            trailingIcon = {
                if (uiState.ageError != null) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Erro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = uiState.isMale,
                onClick = { viewModel.updateGender(true) },
                label = { Text("Masculino") }
            )
            FilterChip(
                selected = !uiState.isMale,
                onClick = { viewModel.updateGender(false) },
                label = { Text("Feminino") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nível de Atividade Física",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val activityLevels = listOf(
            "Sedentário" to 1,
            "Leve" to 2,
            "Moderado" to 3,
            "Intenso" to 4
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            activityLevels.forEach { (label, level) ->
                FilterChip(
                    selected = uiState.activityLevel == level,
                    onClick = { viewModel.updateActivityLevel(level) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveCurrentRecord(context) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isFormValid && uiState.metrics != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isFormValid && uiState.metrics != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                },
                contentColor = if (uiState.isFormValid && uiState.metrics != null) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                }
            )
        ) {
            if (uiState.isFormValid && uiState.metrics != null) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Salvar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("💾 Salvar no Histórico")
            } else {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informação",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Preencha os dados corretamente")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (uiState.isFormValid && uiState.metrics != null) {
            val metrics = uiState.metrics!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Título do Card
                    Text(
                        text = "📊 Resultados",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // IMC
                    ResultItem(
                        title = "IMC",
                        value = String.format("%.1f", metrics.bmi),
                        classification = metrics.bmiClassification
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Peso Ideal (sempre disponível se altura > 0)
                    if (metrics.idealWeight != null && metrics.idealWeight > 0) {
                        ResultItem(
                            title = "Peso Ideal",
                            value = String.format("%.1f kg", metrics.idealWeight),
                            classification = "Para sua altura"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // TMB (apenas se idade for fornecida)
                    if (metrics.bmr != null && metrics.bmr > 0) {
                        ResultItem(
                            title = "TMB",
                            value = String.format("%.0f kcal", metrics.bmr),
                            classification = "Taxa Metabólica Basal"
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Calorias Diárias
                        if (metrics.dailyCalorieNeeds != null && metrics.dailyCalorieNeeds > 0) {
                            ResultItem(
                                title = "Calorias Diárias",
                                value = String.format("%.0f kcal", metrics.dailyCalorieNeeds),
                                classification = "Para manutenção"
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Informação sobre nível de atividade
                            val activityLabel = when (uiState.activityLevel) {
                                1 -> "Sedentário"
                                2 -> "Leve"
                                3 -> "Moderado"
                                4 -> "Intenso"
                                else -> "Não definido"
                            }
                            Text(
                                text = "Baseado no nível: $activityLabel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Mensagem informativa quando não há TMB
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Informação",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Preencha a idade para ver TMB e calorias",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Card indicando que é necessário preencher os dados
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculadora",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Preencha peso e altura para ver os cálculos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ResultItem(title: String, value: String, classification: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = classification,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}