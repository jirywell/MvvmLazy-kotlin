package com.rui.demo.data.source.local

import android.os.AsyncTask
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
        InsertAsyncTask(wordDao).execute(*words)
    }

    override fun updateWords(vararg words: Person) {
        UpdateAsyncTask(wordDao).execute(*words)
    }

    override fun deleteWords(vararg words: Person) {
        DeleteAsyncTask(wordDao).execute(*words)
    }

    override fun deleteAllWords(vararg words: Person) {
        DeleteAllAsyncTask(wordDao).execute()
    }

    override fun getAllWordsLive(): LiveData<List<Person>> {
        return wordDao.allWordsLive
    }

    internal class InsertAsyncTask(private val wordDao: PersonDao) :
        AsyncTask<Person?, Void?, Void?>() {
        override fun doInBackground(vararg words: Person?): Void? {
            wordDao.insertWords(*words)
            return null
        }
    }

    internal class UpdateAsyncTask(private val wordDao: PersonDao) :
        AsyncTask<Person?, Void?, Void?>() {
        override fun doInBackground(vararg words: Person?): Void? {
            wordDao.updateWords(*words)
            return null
        }
    }

    internal class DeleteAsyncTask(private val wordDao: PersonDao) :
        AsyncTask<Person?, Void?, Void?>() {
        override fun doInBackground(vararg words: Person?): Void? {
            wordDao.deleteWords(*words)
            return null
        }
    }

    internal class DeleteAllAsyncTask(private val wordDao: PersonDao) :
        AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            wordDao.deleteAllWords()
            return null
        }
    }

}