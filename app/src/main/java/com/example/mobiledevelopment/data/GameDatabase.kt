package com.example.mobiledevelopment.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PlayerEntity::class,
        GameResultEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class GameDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    abstract fun gameResultDao(): GameResultDao

    companion object {

        @Volatile
        private var instance: GameDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `game_results` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `playerId` INTEGER NOT NULL,
                        `score` INTEGER NOT NULL,
                        `difficulty` TEXT NOT NULL,
                        `speed` INTEGER NOT NULL,
                        `maxCockroaches` INTEGER NOT NULL,
                        `bonusInterval` INTEGER NOT NULL,
                        `roundDuration` INTEGER NOT NULL,
                        `finishedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): GameDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "bugs.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
        }
    }
}