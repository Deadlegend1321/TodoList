package com.example.todo

import android.accounts.AuthenticatorDescription
import android.content.IntentSender
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel (
    var title:String,
    var description: String,
    var category: String,
    var time: Long,
    var date: Long,
    var isFinished: Int = -1,
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
)
