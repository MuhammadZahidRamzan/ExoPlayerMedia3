package com.example.sleepmeditation.utils.service

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SimpleMediaServiceHandler @Inject constructor(
    private val player: ExoPlayer
) : Player.Listener {

    private val _simpleMediaState = MutableStateFlow<SimpleMediaState>(SimpleMediaState.Initial)
    val simpleMediaState = _simpleMediaState.asStateFlow()

    private val _currentMediaItemIndex = MutableStateFlow<Int?>(null)
    val currentMediaItemIndex = _currentMediaItemIndex.asStateFlow()

    private var job: Job? = null

    init {
        player.addListener(this)
        job = Job()
    }

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun seekTo(position: Int){
        player.seekTo(position,0)
        player.play()
    }

    fun seekToNextPrevious(next:Boolean){
        if (next)
            player.seekToNextMediaItem()
        else
            player.seekToPreviousMediaItem()
        player.play()
    }

    fun addMediaItemList(mediaItemList: List<MediaItem>) {
        player.setMediaItems(mediaItemList)
        player.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            PlayerEvent.Backward -> player.seekBack()
            PlayerEvent.Forward -> {
                player.seekForward()
            }
            PlayerEvent.SeekToNext -> player.seekToNextMediaItem()
            PlayerEvent.SeekToPrevious -> player.seekToPreviousMediaItem()
            PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    _currentMediaItemIndex.value = player.currentMediaItemIndex
                    player.pause()
                    stopProgressUpdate()
                } else {
                    player.play()
                    _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }
            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
            is PlayerEvent.SeekTo -> {
                player.seekTo(playerEvent.position,0)
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _simpleMediaState.value =
                SimpleMediaState.Buffering(player.currentPosition)
            ExoPlayer.STATE_READY -> _simpleMediaState.value =
                SimpleMediaState.Ready(player.duration)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = isPlaying)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _simpleMediaState.value = SimpleMediaState.Progress(player.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _simpleMediaState.value = SimpleMediaState.Playing(isPlaying = false)
    }
}

sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object Backward : PlayerEvent()
    object Forward : PlayerEvent()
    object Stop : PlayerEvent()
    object SeekToNext : PlayerEvent()
    object SeekToPrevious : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
    data class SeekTo(val position: Int) : PlayerEvent()
}

sealed class SimpleMediaState {
    object Initial : SimpleMediaState()
    data class Ready(val duration: Long) : SimpleMediaState()
    data class Progress(val progress: Long) : SimpleMediaState()
    data class Buffering(val progress: Long) : SimpleMediaState()
    data class Playing(val isPlaying: Boolean) : SimpleMediaState()
}