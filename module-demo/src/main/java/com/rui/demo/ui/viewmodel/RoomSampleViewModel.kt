package com.rui.demo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rui.demo.data.repository
import com.rui.demo.data.source.local.db.Person
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.ext.launch
import kotlinx.coroutines.*

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
        launch({ repository.insertWords(*words) }, {})
    }

    fun updateWords(vararg words: Person) {
        launch({ repository.updateWords(*words) }, {})

    }

    fun deleteWords(vararg words: Person) {
        launch({ repository.deleteWords(*words) }, {})
    }

    fun deleteAllWords() {
        launch({ repository.deleteAllWords() }, {})
    }
}