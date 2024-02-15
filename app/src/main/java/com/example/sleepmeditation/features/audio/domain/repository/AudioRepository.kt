package com.example.sleepmeditation.features.audio.domain.repository

import com.example.sleepmeditation.features.audio.domain.model.Audio

interface AudioRepository {
    fun getAudioFiles():List<Audio>
}
