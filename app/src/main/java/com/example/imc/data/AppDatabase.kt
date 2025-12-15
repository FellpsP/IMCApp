package com.example.imc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imc.data.dao.BmiDao
import com.example.imc.data.model.BmiData

@Database(
    entities = [BmiData::class],  // Nossa única tabela
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Metodo abstrato que o Room implementará automaticamente
    abstract fun bmiDao(): BmiDao

    // Singleton pattern (padrão de instância única)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Criar banco se não existe
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_database"  // Nome do arquivo .db
                )
                    .fallbackToDestructiveMigration() // Apaga e recria se mudar versão
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}