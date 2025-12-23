package com.example.zeo

import android.app.Application
import com.example.zeo.local.AppDatabase
import com.example.zeo.repository.ExpenseRepository

class ZeoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
}