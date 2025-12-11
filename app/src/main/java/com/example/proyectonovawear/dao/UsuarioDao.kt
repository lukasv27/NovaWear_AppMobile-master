package com.example.proyectonovawear.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectonovawear.model.UsuarioLocal

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuario(usuario: UsuarioLocal)

    @Query("SELECT * FROM usuario_local LIMIT 1")
    suspend fun obtenerUsuario(): UsuarioLocal?

    @Query("DELETE FROM usuario_local")
    suspend fun borrarUsuario()
}
