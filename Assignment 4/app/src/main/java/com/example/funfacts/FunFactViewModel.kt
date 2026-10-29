package com.example.funfacts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class FunFactViewModel(private val repository: FunFactRepository) : ViewModel() {
    private var savedFacts by mutableStateOf<List<FunFact>>(emptyList())

    var searchQuery by mutableStateOf("")
        private set
    var isFetching by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val savedCount: Int get() = savedFacts.size

    // Search the saved facts without calling the API.
    val facts: List<FunFact>
        get() = savedFacts.filter { it.text.contains(searchQuery.trim(), ignoreCase = true) }

    init {
        viewModelScope.launch {
            try {
                repository.facts.collect { savedFacts = it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage = "Could not load saved facts. Please reopen the app."
            }
        }
    }

    fun updateSearch(query: String) {
        searchQuery = query
    }

    fun fetchFact() {
        if (isFetching) return
        isFetching = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.fetchFact()
                // Clear the search so the new fact shows up.
                searchQuery = ""
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage = "Could not fetch a fact. Check your connection and try again."
            } finally {
                isFetching = false
            }
        }
    }

    class Factory(private val repository: FunFactRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FunFactViewModel::class.java)) {
                return FunFactViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
