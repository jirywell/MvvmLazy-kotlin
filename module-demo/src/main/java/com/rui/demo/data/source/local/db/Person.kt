package com.rui.demo.data.source.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rui.demo.data.source.local.db.PersonDao
import com.rui.demo.data.source.local.db.PersonDatabase
import kotlin.jvm.Synchronized

@Entity
class Person(
    @field:ColumnInfo(name = "user_name") var name: String, @field:ColumnInfo(
        name = "user_age"
    ) var age: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}