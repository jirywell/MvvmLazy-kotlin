package com.rui.demo.data.source

import androidx.lifecycle.LiveData
import com.rui.demo.data.source.local.db.Person

/**
 * Created by zjr on 2019/3/26.
 */
interface LocalDataSource {
    fun insertWords(vararg words: Person)
    fun updateWords(vararg words: Person)
    fun deleteWords(vararg words: Person)
    fun deleteAllWords()
    fun getAllWordsLive(): LiveData<List<Person>>
}