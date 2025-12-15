package com.example.imc.data.dao

import androidx.room.*
import com.example.imc.data.model.BmiData
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiDao {

    // Inserir novo registro
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BmiData): Long

    // Buscar todos os registros ordenados por data (mais recente primeiro)
    @Query("SELECT * FROM bmi_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BmiData>>

    // Buscar registro por ID
    @Query("SELECT * FROM bmi_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BmiData?
    // Função auxiliar
    @Query("SELECT COUNT(*) FROM bmi_records")
    suspend fun getRecordCount(): Int

    // Deletar registro
    @Delete
    suspend fun delete(record: BmiData)

    // Deletar todos os registros (para limpeza)
    @Query("DELETE FROM bmi_records")
    suspend fun deleteAll()

}