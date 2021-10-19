package com.rui.demo.ui.viewmodel

import androidx.lifecycle.LiveData
import com.rui.demo.data.repository
import com.rui.demo.data.source.local.db.Person
import com.rui.mvvmlazy.base.BaseViewModel

/**
 * Create Date：2021/01/01
 * 实现Room数据的基本操作
 * zjr
 */
class RoomSampleViewModel() :
    BaseViewModel() {
    val allWordsLive: LiveData<List<Person>>
        get() = repository.getAllWordsLive()

    fun insertWords(vararg words: Person) {
        repository.insertWords(*words)
    }

    fun updateWords(vararg words: Person) {
        repository.updateWords(*words)
    }

    fun deleteWords(vararg words: Person) {
        repository.deleteWords(*words)
    }

    fun deleteAllWords() {
        repository.deleteAllWords()
    }
}