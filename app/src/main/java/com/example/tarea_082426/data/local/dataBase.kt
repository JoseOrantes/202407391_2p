package com.example.tarea_082426.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.tarea_082426.data.local.dao.LoginDao
import com.example.tarea_082426.data.local.dao.Profile
import com.example.tarea_082426.data.local.dao.ProfileDao
import com.example.tarea_082426.data.local.entities.User

// @Database(entities = [User::class], version = 3)
// abstract class AppDatabase : RoomDatabase() {
//     abstract fun loginDao(): LoginDao
// }
@Database(entities = [User::class, Profile::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao
    abstract fun profileDao(): ProfileDao
}

object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "db_desarrollomovil"
            )
                .setDriver(AndroidSQLiteDriver())
                .fallbackToDestructiveMigration() // Limpia la DB si hay cambios de versión
                .build()
            INSTANCE = instance
            instance
        }
    }
}
