package com.ankit.media3.sample.backend

import com.ankit.media3.sample.R
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.let

@AndroidEntryPoint(MediaSessionService::class)
class MediaPlaybackService @Inject internal constructor(): Hilt_MediaPlaybackService() {
    @Inject @ApplicationContext internal lateinit var context: Context
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(context).build()
        player.addMediaItems(getMediaItems())
        player.prepare()

        val callback = MediaSessionCallbackImpl()
        mediaSession = MediaSession.Builder(context, player)
            .setCallback(callback)
            .setMediaButtonPreferences(listOf(DELETE_COMMAND_BUTTON, NEXT_COMMAND_BUTTON))
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onDestroy() {
        mediaSession?.let {
            it.player.release()
            it.release()
            mediaSession = null
        }
        super.onDestroy()
    }

    private fun getMediaItems(): List<MediaItem> {
        val rawResourceUri = context.resourceUri(R.raw.media_item_1)
        val mediaItem = MediaItem.fromUri(rawResourceUri)
        return listOf(mediaItem)
    }

    private fun Context.resourceUri(resourceId: Int): Uri = with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

    private class MediaSessionCallbackImpl : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> = Futures.immediateFuture(mediaItems)

        @OptIn(UnstableApi::class)
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Log.i("Ankit", "Accepting connection from ${controller.packageName}")
            val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                .buildUpon()
                .addSessionCommands(listOf(NEXT_SESSION_COMMAND, DELETE_SESSION_COMMAND))
                .build()
            return MediaSession.ConnectionResult.accept(
                sessionCommands,
                MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
            )
        }
    }

    private companion object {
        const val DELETE_COMMAND = "Delete"
        val DELETE_SESSION_COMMAND = SessionCommand(DELETE_COMMAND, Bundle.EMPTY)
        val DELETE_COMMAND_BUTTON = CommandButton.Builder()
            .setSessionCommand(DELETE_SESSION_COMMAND)
            .setDisplayName(DELETE_COMMAND)
            .setCustomIconResId(android.R.drawable.ic_delete)
            .build()

        const val NEXT_COMMAND = "Next"
        val NEXT_SESSION_COMMAND = SessionCommand(NEXT_COMMAND, Bundle.EMPTY)
        val NEXT_COMMAND_BUTTON = CommandButton.Builder()
            .setSessionCommand(NEXT_SESSION_COMMAND)
            .setDisplayName(NEXT_COMMAND)
            .setCustomIconResId(android.R.drawable.ic_media_next)
            .build()
    }
}