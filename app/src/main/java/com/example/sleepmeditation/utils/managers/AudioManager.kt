package com.za.filemanagerapp.utils.managers

import com.example.sleepmeditation.features.audio.domain.model.Audio

interface AudioManager {

    fun play()
    fun pause()
    fun setAudio(audio: Audio)
    fun setAudios(audios:List<Audio>)
    fun playNext()
    fun playPrevious()
    fun release()
    fun setCallbacks(callbacks:AudioManagerCallbacks)
    fun isAudioPlaying():Boolean
    fun seekTo(progress: Long)
}
