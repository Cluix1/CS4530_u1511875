package com.example.funfacts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow

// Fetch facts from the API and save them in Room.
class FunFactRepository private constructor(
    private val dao: FunFactDao,
    private val client: HttpClient
) {
    val facts: Flow<List<FunFact>> = dao.observeAll()

    suspend fun fetchFact() {
        val response: FunFactResponse = client.get(
            "https://uselessfacts.jsph.pl/api/v2/facts/random"
        ) {
            parameter("language", "en")
        }.body()
        dao.insert(FunFact(text = response.text))
    }

    companion object {
        @Volatile private var instance: FunFactRepository? = null

        fun getInstance(dao: FunFactDao, client: HttpClient): FunFactRepository =
            instance ?: synchronized(this) {
                instance ?: FunFactRepository(dao, client).also { instance = it }
            }
    }
}
