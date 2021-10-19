package com.rui.demo.data.source.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//singleton
@Database(entities = [Person::class], version = 1, exportSchema = false)
abstract class PersonDatabase : RoomDatabase() {
    abstract val wordDao: PersonDao

    companion object {
        private var INSTANCE: PersonDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PersonDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    PersonDatabase::class.java,
                    "database"
                ).build()
            }
            return INSTANCE
        }
    }
}