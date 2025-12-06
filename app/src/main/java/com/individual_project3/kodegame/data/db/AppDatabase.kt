package com.individual_project3.kodegame.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.individual_project3.kodegame.data.model.Child
import com.individual_project3.kodegame.data.model.Parent
import com.individual_project3.kodegame.data.progress.ProgressDao
import com.individual_project3.kodegame.data.progress.ProgressRecord


@Database(
    entities = [
        Parent::class,
        Child::class,
        ProgressRecord::class         // ← NEW TABLE
    ],
    version = 4,                      // ← Version bump
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun authDao(): AuthDao
    abstract fun progressDao(): ProgressDao




    companion object{
        @Volatile private var INSTANCE: AppDatabase? = null
        val migration_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                childId INTEGER NOT NULL,
                level INTEGER NOT NULL,
                strawberries INTEGER NOT NULL,
                completed INTEGER NOT NULL
            );
        """.trimIndent())
            }
        }

        //returns a singleton AppDatabase Instance. use this when not using DI
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also{INSTANCE = it}
            }
        private fun buildDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kodegame_db"
            ).addMigrations(migration_3_4)
                .build()
    }
}