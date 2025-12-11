package com.example.proyectonovawear.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectonovawear.dao.UsuarioDao
import com.example.proyectonovawear.model.UsuarioLocal

@Database(entities = [UsuarioLocal::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "usuario_local_db"
                ).build().also { INSTANCE = it }
            }
    }
}
