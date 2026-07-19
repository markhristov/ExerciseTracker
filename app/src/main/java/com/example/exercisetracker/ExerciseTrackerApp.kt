package com.example.exercisetracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
@Composable
fun ExerciseTrackerApp(
     viewModel: PoseViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        AppBar()
    }) { paddingValues ->

        ExerciseScreen(modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun ExerciseScreen(modifier: Modifier = Modifier) {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Exercise tracker app") },
        modifier = modifier
    )
}