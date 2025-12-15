package com.example.imc.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imc.ui.viewmodel.DetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recordId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Carrega o registro quando a tela é aberta ou quando o ID muda
    LaunchedEffect(recordId) {
        viewModel.loadRecord(context, recordId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalhes da Medição",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Estados possíveis da tela
        when {
            uiState.isLoading -> {
                // Carregando
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregando dados...")
                    }
                }
            }

            uiState.error != null -> {
                // Erro
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "❌",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Voltar")
                        }
                    }
                }
            }

            uiState.record != null -> {
                // Dados carregados com sucesso
                val record = uiState.record!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cabeçalho com data
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = formatDateTime(record.timestamp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Data e hora da medição",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Dados básicos
                    InfoGrid(
                        title = "Dados Pessoais",
                        items = listOf(
                            InfoItem("Peso", "${String.format("%.1f", record.weight)} kg"),
                            InfoItem("Altura", "${String.format("%.0f", record.height)} cm"),
                            InfoItem("Idade", "${record.age} anos"),
                            InfoItem("Gênero", if (record.isMale) "Masculino" else "Feminino"),
                            InfoItem("Atividade", getActivityLevelText(record.activityLevel))
                        )
                    )

                    // IMC com cor
                    BmiCard(bmi = record.bmi, classification = record.classification)

                    // TMB (se disponível)
                    if (record.bmr > 0) {
                        MetricCard(
                            title = "Taxa Metabólica Basal (TMB)",
                            value = "${String.format("%.0f", record.bmr)} kcal",
                            description = "Calorias necessárias em repouso (fórmula Mifflin-St Jeor)",
                            icon = "🔥"
                        )
                    }

                    // Peso Ideal
                    if (record.idealWeight > 0) {
                        MetricCard(
                            title = "Peso Ideal",
                            value = "${String.format("%.1f", record.idealWeight)} kg",
                            description = "Calculado pela fórmula de Devine",
                            icon = "⚖️"
                        )
                    }

                    // Necessidade Calórica
                    if (record.dailyCalorieNeeds > 0) {
                        MetricCard(
                            title = "Necessidade Calórica Diária",
                            value = "${String.format("%.0f", record.dailyCalorieNeeds)} kcal",
                            description = "Baseado na TMB e nível de atividade",
                            icon = "🍎"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// Componente para exibir grade de informações
@Composable
fun InfoGrid(
    title: String,
    items: List<InfoItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Componente para exibir IMC com cor
@Composable
fun BmiCard(bmi: Double, classification: String) {
    val (color, emoji) = when {
        bmi < 18.5 -> Pair(MaterialTheme.colorScheme.secondary, "😟")
        bmi < 25 -> Pair(MaterialTheme.colorScheme.primary, "😊")
        bmi < 30 -> Pair(MaterialTheme.colorScheme.tertiary, "😐")
        bmi < 35 -> Pair(MaterialTheme.colorScheme.error.copy(alpha = 0.8f), "😟")
        else -> Pair(MaterialTheme.colorScheme.error, "😨")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$emoji Índice de Massa Corporal (IMC)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = String.format("%.2f", bmi),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = classification,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Classificação detalhada
            val classificationDetail = when {
                bmi < 18.5 -> "Abaixo do peso ideal. Considere acompanhamento nutricional."
                bmi < 25 -> "Peso dentro da faixa considerada saudável."
                bmi < 30 -> "Sobrepeso. Atenção à alimentação e prática de exercícios."
                bmi < 35 -> "Obesidade Grau I. Recomenda-se acompanhamento médico."
                bmi < 40 -> "Obesidade Grau II. Busque orientação profissional."
                else -> "Obesidade Grau III. É importante buscar ajuda médica."
            }

            Text(
                text = classificationDetail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Componente para métricas gerais
@Composable
fun MetricCard(
    title: String,
    value: String,
    description: String,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = icon,
                    modifier = Modifier.padding(end = 8.dp),
                    fontSize = 20.sp
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Data class para itens de informação
data class InfoItem(val label: String, val value: String)

// Função auxiliar para formatar data/hora
fun formatDateTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
    return formatter.format(date)
}

// Função auxiliar para obter texto do nível de atividade
fun getActivityLevelText(level: Int): String {
    return when (level) {
        1 -> "Sedentário"
        2 -> "Leve"
        3 -> "Moderado"
        4 -> "Intenso"
        else -> "Não informado"
    }
}