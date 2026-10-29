package com.example.funfacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FunFactScreen(viewModel: FunFactViewModel) {
    val facts = viewModel.facts

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Fun Facts", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Fetch random facts and read them again anytime.",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = viewModel::fetchFact,
                enabled = !viewModel.isFetching,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (viewModel.isFetching) "Fetching…" else "Fetch a new fact")
            }
            if (viewModel.isFetching) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            viewModel.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::updateSearch,
                label = { Text("Search saved facts") },
                supportingText = { Text("Filter your saved facts by keyword") },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        TextButton(onClick = { viewModel.updateSearch("") }) {
                            Text("Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                if (viewModel.searchQuery.isBlank()) "Saved facts (${viewModel.savedCount})"
                else "Results (${facts.size} of ${viewModel.savedCount})",
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (facts.isEmpty()) {
                    item {
                        Text(
                            if (viewModel.savedCount == 0) "No facts saved yet. Fetch one to get started!"
                            else "No saved facts match your search."
                        )
                    }
                }
                items(facts, key = { it.id }) { fact ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            fact.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }
        }
    }
}
