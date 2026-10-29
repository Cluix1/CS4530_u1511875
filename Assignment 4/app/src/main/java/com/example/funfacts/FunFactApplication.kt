package com.example.funfacts

import android.app.Application

class FunFactApplication : Application() {
    val database by lazy { FunFactDatabase.getInstance(this) }
    val client by lazy { ApiClient.client }
    val repository by lazy {
        FunFactRepository.getInstance(database.funFactDao(), client)
    }
}
