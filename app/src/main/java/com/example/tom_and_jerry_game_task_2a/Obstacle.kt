package com.example.tom_and_jerry_game_task_2a

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

data class Obstacle(
    val type : String,
    val lane : Int,
    val yOffset : MutableState<Float>,
    val inScreen : MutableState<Boolean>
)
