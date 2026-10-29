package com.example.funfacts

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class FunFactAppTest {
    @get:Rule
    val composeRule = createComposeRule()
    private val viewModelStore = ViewModelStore()

    @Before
    fun resetFacts() {
        database.clearAllTables()
        requests.set(0)
        failRequest = false
    }

    @After
    fun clearViewModel() {
        composeRule.runOnIdle { viewModelStore.clear() }
    }

    private fun showScreen(): FunFactViewModel {
        val viewModel = composeRule.runOnIdle {
            ViewModelProvider(viewModelStore, FunFactViewModel.Factory(repository))[
                FunFactViewModel::class.java
            ]
        }
        composeRule.setContent { MaterialTheme { FunFactScreen(viewModel) } }
        return viewModel
    }

    @Test
    fun fetchSavesFactAndImmediatelyDisplaysIt() {
        val viewModel = showScreen()
        composeRule.onNodeWithText("Search saved facts").performTextReplacement("old search")
        composeRule.onNodeWithText("Fetch a new fact").performClick()
        composeRule.waitUntil(10_000) { viewModel.savedCount == 1 && !viewModel.isFetching }

        composeRule.onNodeWithText("Octopuses have three hearts.").assertIsDisplayed()
        assertEquals("", viewModel.searchQuery)
        assertEquals(1, requests.get())
        assertEquals("https://uselessfacts.jsph.pl/api/v2/facts/random?language=en", requestedUrl)
        runBlocking {
            assertEquals("Octopuses have three hearts.", database.funFactDao().observeAll().first().single().text)
        }
        assertSame(repository, FunFactRepository.getInstance(database.funFactDao(), client))
    }

    @Test
    fun searchFiltersLocalFactsAsYouTypeWithoutNetworkRequests() {
        runBlocking {
            database.funFactDao().insert(FunFact(text = "Octopuses have three hearts."))
            database.funFactDao().insert(FunFact(text = "Honey can last a long time."))
            database.funFactDao().insert(FunFact(text = "A 100% literal search example."))
        }
        val viewModel = showScreen()
        composeRule.waitUntil(10_000) { viewModel.savedCount == 3 }
        composeRule.onNodeWithText("Search saved facts").performTextReplacement("HEART")
        composeRule.onNodeWithText("Octopuses have three hearts.").assertIsDisplayed()
        composeRule.onNodeWithText("Honey can last a long time.").assertDoesNotExist()
        composeRule.onNodeWithText("Search saved facts").performTextReplacement("missing")
        composeRule.onNodeWithText("No saved facts match your search.").assertIsDisplayed()
        composeRule.onNodeWithText("Search saved facts").performTextReplacement("%")
        composeRule.onNodeWithText("A 100% literal search example.").assertIsDisplayed()
        composeRule.onNodeWithText("Clear").performClick()
        composeRule.onNodeWithText("Honey can last a long time.").assertIsDisplayed()
        assertEquals(0, requests.get())
    }

    @Test
    fun failedFetchKeepsSavedFactsAndAllowsRetry() {
        runBlocking { database.funFactDao().insert(FunFact(text = "An already saved fact.")) }
        failRequest = true
        val viewModel = showScreen()
        composeRule.onNodeWithText("Fetch a new fact").performClick()
        composeRule.waitUntil(10_000) { viewModel.errorMessage != null && !viewModel.isFetching }
        composeRule.onNodeWithText("Could not fetch a fact. Check your connection and try again.")
            .assertIsDisplayed()
        composeRule.onNodeWithText("An already saved fact.").assertIsDisplayed()
        assertEquals(1, viewModel.savedCount)

        failRequest = false
        composeRule.onNodeWithText("Fetch a new fact").performClick()
        composeRule.waitUntil(10_000) { viewModel.savedCount == 2 && !viewModel.isFetching }
        composeRule.onNodeWithText("Octopuses have three hearts.").assertIsDisplayed()
    }

    @Test
    fun factsSurviveClosingAndReopeningDatabase() = runBlocking {
        val name = "fun-facts-persistence-test.db"
        context.deleteDatabase(name)
        try {
            val firstDatabase = Room.databaseBuilder(context, FunFactDatabase::class.java, name).build()
            val saved = try {
                firstDatabase.funFactDao().insert(FunFact(text = "Saved across reopening."))
                firstDatabase.funFactDao().observeAll().first().single()
            } finally {
                firstDatabase.close()
            }
            val reopenedDatabase = Room.databaseBuilder(context, FunFactDatabase::class.java, name).build()
            try {
                assertEquals(listOf(saved), reopenedDatabase.funFactDao().observeAll().first())
            } finally {
                reopenedDatabase.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    companion object {
        private val context: Context = ApplicationProvider.getApplicationContext()
        private val database = Room.inMemoryDatabaseBuilder(context, FunFactDatabase::class.java).build()
        private val requests = AtomicInteger(0)
        @Volatile private var failRequest = false
        @Volatile private var requestedUrl = ""
        private val client = HttpClient(MockEngine {
            requests.incrementAndGet()
            requestedUrl = it.url.toString()
            delay(500)
            respond(
                content = """{"id":"api-id","text":"Octopuses have three hearts.","language":"en"}""",
                status = if (failRequest) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        private val repository = FunFactRepository.getInstance(database.funFactDao(), client)

        @JvmStatic
        @AfterClass
        fun closeResources() {
            client.close()
            database.close()
        }
    }
}
