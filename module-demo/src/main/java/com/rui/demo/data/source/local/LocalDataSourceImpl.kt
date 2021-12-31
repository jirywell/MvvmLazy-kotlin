package com.rui.demo.data.source.local

import androidx.lifecycle.LiveData
import com.rui.demo.data.source.LocalDataSource
import com.rui.demo.data.source.local.db.Person
import com.rui.demo.data.source.local.db.PersonDao
import com.rui.demo.data.source.local.db.PersonDatabase
import com.rui.mvvmlazy.base.appContext

/**
 * 本地数据源，可配合Room框架使用
 * Created by zjr on 2019/3/26.
 */
class LocalDataSourceImpl : LocalDataSource {
    private val wordDao: PersonDao by lazy {
        PersonDatabase.getDatabase(appContext)!!.wordDao
    }


    override fun insertWords(vararg words: Person) {
        wordDao.insertWords(*words)
    }

    override fun updateWords(vararg words: Person) {
        wordDao.updateWords(*words)
    }

    override fun deleteWords(vararg words: Person) {
        wordDao.deleteWords(*words)
    }

    override fun deleteAllWords() {
        wordDao.deleteAllWords()
    }

    override fun getAllWordsLive(): LiveData<List<Person>> {
        return wordDao.allWordsLive
    }

}