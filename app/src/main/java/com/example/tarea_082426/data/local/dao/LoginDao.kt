package com.example.tarea_082426.data.local.entities

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
// data class User(
//     @PrimaryKey val id_user: Int,
//     val nombre: String,
//     val apellido: String,
//     val usuario: String,
//     val password: String
// )
data class User(
    @PrimaryKey val id_user: Int,
    val nombre: String,
    val apellido: String,
    val usuario: String,
    val password: String,
    val email: String = "",
    val id_rol: Int = 0,
    val rol: String = ""
)