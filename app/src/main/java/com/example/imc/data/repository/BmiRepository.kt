package com.example.imc.data.repository

import com.example.imc.data.dao.BmiDao
import com.example.imc.data.model.BmiData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BmiRepository @Inject constructor(
    private val bmiDao: BmiDao
) {

    // Salvar um registro
    suspend fun insertRecord(record: BmiData): Long {
        return bmiDao.insert(record)
    }

    // Obter todos os registros (Flow observa mudanças automaticamente)
    fun getAllRecords(): Flow<List<BmiData>> {
        return bmiDao.getAllRecords()
    }

    // Obter um registro específico
    suspend fun getRecordById(id: Long): BmiData? {
        return bmiDao.getRecordById(id)
    }

    // Deletar um registro
    suspend fun deleteRecord(record: BmiData) {
        bmiDao.delete(record)
    }

    // Função auxiliar: criar e salvar registro a partir dos dados atuais
    suspend fun saveCurrentMetrics(
        weight: Double,
        height: Double,
        age: Int,
        isMale: Boolean,
        activityLevel: Int,
        bmi: Double,
        classification: String,
        bmr: Double = 0.0,
        idealWeight: Double = 0.0,
        dailyCalorieNeeds: Double = 0.0
    ): Long {
        val record = BmiData(
            weight = weight,
            height = height,
            bmi = bmi,
            classification = classification,
            bmr = bmr,
            idealWeight = idealWeight,
            dailyCalorieNeeds = dailyCalorieNeeds,
            age = age,
            isMale = isMale,
            activityLevel = activityLevel
        )
        return insertRecord(record)
    }
}