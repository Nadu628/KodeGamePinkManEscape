package com.individual_project3.kodegame.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.individual_project3.kodegame.data.model.Child
import com.individual_project3.kodegame.data.model.Parent

@Database(entities = [Parent::class, Child::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun authDao(): AuthDao

    companion object{
        @Volatile private var INSTANCE: AppDatabase? = null

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
            ).fallbackToDestructiveMigration()
                .build()
    }
}