package com.za.filemanagerapp.utils.managers

import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.utils.enums.AudioState

interface AudioManagerCallbacks {
    fun onProgressChange(progress :Long)
    fun onAudioChange(audio: Audio)
    fun onStateChange(state : AudioState)
}