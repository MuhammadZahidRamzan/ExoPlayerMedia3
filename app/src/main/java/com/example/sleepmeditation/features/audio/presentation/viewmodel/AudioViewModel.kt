package com.example.sleepmeditation.features.audio.presentation.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.features.audio.domain.repository.AudioRepository
import com.example.sleepmeditation.utils.Utils.formatDuration
import com.example.sleepmeditation.utils.service.PlayerEvent
import com.example.sleepmeditation.utils.service.SimpleMediaServiceHandler
import com.example.sleepmeditation.utils.service.SimpleMediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: AudioRepository,
    private val simpleMediaServiceHandler: SimpleMediaServiceHandler
) : ViewModel() {
    val duration = MutableLiveData(0L)
    val progress = MutableLiveData(0f)
    val progressString = MutableLiveData("00:00")
    val isPlaying = MutableLiveData(false)
    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()

    private var _audiosList = MutableLiveData<List<Audio>>()
    val audiosList: LiveData<List<Audio>>
        get() = _audiosList

    private var _currentMediaItemIndex = MutableLiveData<Int?>()
    val currentMediaItemIndex: LiveData<Int?>
        get() = _currentMediaItemIndex


    init {
        viewModelScope.launch {
            simpleMediaServiceHandler.simpleMediaState.collect { mediaState ->
                when (mediaState) {
                    is SimpleMediaState.Buffering -> calculateProgressValues(mediaState.progress)
                    SimpleMediaState.Initial -> _uiState.value = UIState.Initial
                    is SimpleMediaState.Playing -> {
                        isPlaying.value = mediaState.isPlaying
                        _currentMediaItemIndex.value = simpleMediaServiceHandler.currentMediaItemIndex.value
                    }
                    is SimpleMediaState.Progress -> calculateProgressValues(mediaState.progress)
                    is SimpleMediaState.Ready -> {
                        duration.value = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
    }

    fun seekToNextPrevious(next:Boolean){
        simpleMediaServiceHandler.seekToNextPrevious(next)
    }

    fun onUIEvent(uiEvent: UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            UIEvent.Backward -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Backward)
            }
            UIEvent.Forward -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.Forward)
            }
            UIEvent.PlayPause -> {
                simpleMediaServiceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            }
            is UIEvent.UpdateProgress -> {
                progress.value = uiEvent.newProgress
                simpleMediaServiceHandler.onPlayerEvent(
                    PlayerEvent.UpdateProgress(
                        uiEvent.newProgress
                    )
                )
            }
            is UIEvent.SeekTo -> {
                PlayerEvent.SeekTo(uiEvent.position)
            }

            UIEvent.SeekToNext -> PlayerEvent.SeekToNext
            UIEvent.SeekToPrevious -> PlayerEvent.SeekToPrevious
        }
    }

    fun seekTo(position: Int){
        simpleMediaServiceHandler.seekTo(position)
    }

    private fun calculateProgressValues(currentProgress: Long) {
        progress.value =
            if (currentProgress > 0) (currentProgress.toFloat() / duration.value!!) else 0f
        progressString.value = formatDuration(currentProgress)
    }

    fun getAudiosList() {
        viewModelScope.launch(Dispatchers.Main) {
            val audios = repository.getAudioFiles()
            val mediaItemList = mutableListOf<MediaItem>()
            audios.forEach { audio ->

                val mediaItem = MediaItem.Builder()
                    .setUri(audio.artUri)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setFolderType(MediaMetadata.FOLDER_TYPE_ALBUMS)
                            .setArtworkUri(audio.image?.toUri())
                            .setAlbumTitle(audio.album)
                            .setDisplayTitle(audio.title)
                            .build()
                    ).build()
                mediaItemList.add(mediaItem)

//                audio.artUri?.let { MediaItem.fromUri(it) }?.let {
//
//                    mediaItemList.add(
//                        it
//                    )
//                }
            }
            simpleMediaServiceHandler.addMediaItemList(mediaItemList)
            _audiosList.postValue(audios)
        }
    }
}

sealed class UIEvent {
    data object PlayPause : UIEvent()
    data object Backward : UIEvent()
    data object Forward : UIEvent()
    data object SeekToNext : UIEvent()
    data object SeekToPrevious : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
    data class SeekTo(val position: Int) : UIEvent()
}

sealed class UIState {
    data object Initial : UIState()
    data object Ready : UIState()
}