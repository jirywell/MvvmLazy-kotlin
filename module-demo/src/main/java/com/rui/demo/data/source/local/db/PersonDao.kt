package com.rui.demo.data.source.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rui.demo.data.source.local.db.PersonDao
import com.rui.demo.data.source.local.db.PersonDatabase
import kotlin.jvm.Synchronized

@Dao // Database access object
interface PersonDao {
    @Insert
    fun insertWords(vararg people: Person?)

    @Update
    fun updateWords(vararg people: Person?)

    @Delete
    fun deleteWords(vararg people: Person?)

    @Query("DELETE FROM Person")
    fun deleteAllWords()

    @get:Query("SELECT * FROM Person ORDER BY ID DESC")
    val allWordsLive: LiveData<List<Person>>
}