package com.example.mobiledevelopment.session

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionPlayer(
    val id: Long,
    val name: String,
    val isGuest: Boolean
)

class SessionViewModel : ViewModel() {

    private val _currentPlayer =
        MutableStateFlow<SessionPlayer?>(null)

    val currentPlayer = _currentPlayer.asStateFlow()

    fun selectPlayer(player: SessionPlayer) {
        _currentPlayer.value = player
    }

    fun clearPlayer() {
        _currentPlayer.value = null
    }
}