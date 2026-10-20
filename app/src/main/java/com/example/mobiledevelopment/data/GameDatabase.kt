package com.example.mobiledevelopment.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlayerEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GameDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var instance: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "bugs.db"
                )
                    .build()
                    .also { database ->
                        instance = database
                    }
            }
        }
    }
}