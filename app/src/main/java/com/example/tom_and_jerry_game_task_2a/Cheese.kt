package com.example.tom_and_jerry_game_task_2a

import androidx.compose.runtime.MutableState

data class Cheese(
    val lane : Int,
    val yOffset : MutableState<Int>,
    val inScreen : MutableState<Boolean>
)
