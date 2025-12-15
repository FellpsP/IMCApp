package com.example.imc.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imc.data.AppDatabase
import com.example.imc.data.model.BmiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de histórico.
 * Responsável por carregar e gerenciar a lista de registros do banco de dados.
 */
class HistoryViewModel : ViewModel() {

    // Estado da UI - começa vazio
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    /**
     * Inicia a observação do banco de dados.
     * O Flow emitirá automaticamente quando os dados mudarem.
     */
    fun startObservingRecords(context: Context) {
        viewModelScope.launch {
            try {
                // Atualiza estado para "carregando"
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Obtém o banco de dados
                val database = AppDatabase.getDatabase(context)

                // Coleta o Flow do DAO
                database.bmiDao().getAllRecords().collectLatest { records ->
                    // Este bloco é executado SEMPRE que os dados mudam
                    _uiState.value = HistoryUiState(
                        records = records,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao carregar histórico: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    /**
     * Deleta um registro do banco de dados.
     */
    fun deleteRecord(context: Context, recordId: Long) {
        viewModelScope.launch {
            try {
                val database = AppDatabase.getDatabase(context)

                // Busca o registro primeiro
                val record = database.bmiDao().getRecordById(recordId)

                if (record != null) {
                    // Deleta o registro
                    database.bmiDao().delete(record)
                    // O Flow observará a mudança automaticamente
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao deletar: ${e.message}"
                )
            }
        }
    }
}

/**
 * Estado da UI para a tela de histórico.
 * @param records Lista de registros do banco de dados
 * @param isLoading Indica se está carregando dados
 * @param error Mensagem de erro, se houver
 */
data class HistoryUiState(
    val records: List<BmiData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)