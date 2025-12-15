package com.example.imc.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imc.data.AppDatabase
import com.example.imc.data.model.BmiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de detalhes de um registro.
 * Responsável por carregar os dados de um registro específico.
 */
class DetailViewModel : ViewModel() {

    // Estado da UI
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    /**
     * Carrega um registro específico do banco de dados.
     * @param context Contexto da aplicação
     * @param recordId ID do registro a ser carregado
     */
    fun loadRecord(context: Context, recordId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val database = AppDatabase.getDatabase(context)
                val record = database.bmiDao().getRecordById(recordId)

                if (record != null) {
                    _uiState.value = DetailUiState(
                        record = record,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = DetailUiState(
                        record = null,
                        isLoading = false,
                        error = "Registro não encontrado"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    error = "Erro ao carregar: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}

/**
 * Estado da UI para a tela de detalhes.
 * @param record O registro carregado (ou null se não encontrado)
 * @param isLoading Indica se está carregando
 * @param error Mensagem de erro, se houver
 */
data class DetailUiState(
    val record: BmiData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)