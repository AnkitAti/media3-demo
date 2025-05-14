package com.ankit.media3.sample.ui.data

import androidx.media3.session.MediaController

data class PlayerState(
    val isPlaying: Boolean
) {
    companion object {
        val EMPTY: PlayerState = PlayerState(isPlaying = false)

        fun fromController(controller: MediaController) =
            PlayerState(isPlaying = controller.isPlaying)
    }
}