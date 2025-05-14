package com.ankit.media3.sample.ui

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.ankit.media3.sample.backend.MediaPlaybackService
import com.ankit.media3.sample.ui.data.PlayerState
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject internal constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _controllerFlow = MutableStateFlow<MediaController?>(null)
    val controllerFlow = _controllerFlow.asStateFlow()

    fun fetchController() {
        val sessionToken = SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            _controllerFlow.tryEmit(controllerFuture.get())
        }, MoreExecutors.directExecutor())
    }

    fun togglePlayPause() {
        val controller = controllerFlow.value
        if (controller != null && controller.isPlaying) {
            controller.pause()
        } else {
            controller?.play()
        }
    }
}