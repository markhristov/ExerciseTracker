package com.example.exercisetracker

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PoseViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PoseUiState())
    val uiState = _uiState

    fun toggleWorkout() {
        if (_uiState.value.isRecording) {
            stopWorkout()
        } else {
            startWorkout()
        }
    }

    fun startWorkout() {
        _uiState.update {
            it.copy(isRecording = true)
        }
    }

    fun stopWorkout() {
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    fun onRepDetected() {
        _uiState.update {
            it.copy(
                repCount = it.repCount + 1
            )
        }
    }
}

data class PoseUiState(
    val isRecording: Boolean = false,
    val repCount: Int = 0
)