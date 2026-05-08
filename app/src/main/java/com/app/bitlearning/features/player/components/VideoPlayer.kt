/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.player.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import com.app.bitlearning.core.common.theme.OnBackground
import kotlinx.coroutines.delay

private const val TAG = "BLVideoPlayer"

@OptIn(UnstableApi::class)
@Composable
fun BLVideoPlayer(
    videoUrl: String?,
    authToken: String?,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    startPositionSeconds: Int = 0,
    onProgressSync: ((Int, Int) -> Unit)? = null,
    isFullscreen: Boolean = false,
    onFullscreenChange: ((Boolean) -> Unit)? = null,
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val httpFactory = remember(authToken) {
        val headers = mutableMapOf<String, String>()
        if (!authToken.isNullOrBlank()) {
            headers["Authorization"] = "Bearer $authToken"
        }
        headers["Accept"] = "application/vnd.apple.mpegurl, application/octet-stream, */*"
        DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(headers)
    }

    val exoPlayer = remember(authToken) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = autoPlay
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    // Sync orientation with fullscreen state
    LaunchedEffect(isFullscreen) {
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        // Immersive mode for fullscreen
        val window = activity?.window
        if (isFullscreen) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Restore orientation when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    LaunchedEffect(autoPlay) {
        exoPlayer.playWhenReady = autoPlay
    }

    LaunchedEffect(videoUrl) {
        if (!videoUrl.isNullOrBlank()) {
            val mediaItem = MediaItem.Builder()
                .setUri(videoUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build()
            val mediaSource = HlsMediaSource.Factory(httpFactory).createMediaSource(mediaItem)
            Log.i(TAG, "Preparing HLS source: $videoUrl")
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
        }
    }

    LaunchedEffect(exoPlayer, onProgressSync) {
        while (true) {
            delay(5_000)
            val duration = exoPlayer.duration
            if (onProgressSync != null && exoPlayer.isPlaying && duration > 0) {
                onProgressSync(exoPlayer.currentPosition.toInt() / 1000, duration.toInt() / 1000)
            }
        }
    }

    // Capture the seek position at the moment the video URL changes — not updated on every sync
    val initialSeekSeconds = remember(videoUrl) { startPositionSeconds }

    DisposableEffect(exoPlayer, videoUrl) {
        var hasAppliedInitialSeek = false
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                val state = when (playbackState) {
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
                    Player.STATE_ENDED -> "ENDED"
                    else -> "UNKNOWN($playbackState)"
                }
                Log.d(TAG, "Playback state=$state url=$videoUrl")
                if (playbackState == Player.STATE_READY && !hasAppliedInitialSeek && initialSeekSeconds > 0) {
                    hasAppliedInitialSeek = true
                    exoPlayer.seekTo(initialSeekSeconds * 1000L)
                    Log.d(TAG, "Applied initial seek to ${initialSeekSeconds}s")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Playback error url=$videoUrl type=${error.errorCodeName} cause=${error.cause?.javaClass?.simpleName ?: "none"}: ${error.cause?.message ?: error.message}")
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    DisposableEffect(Unit) {
        onDispose {
            val duration = exoPlayer.duration
            if (onProgressSync != null && duration > 0 && exoPlayer.currentPosition > 0) {
                onProgressSync(exoPlayer.currentPosition.toInt() / 1000, duration.toInt() / 1000)
            }
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.background(OnBackground)) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    useController = true
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setFullscreenButtonClickListener { entering ->
                        onFullscreenChange?.invoke(entering)
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
