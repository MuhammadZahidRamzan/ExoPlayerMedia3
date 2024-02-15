package com.za.filemanagerapp.utils.managers

import com.example.sleepmeditation.features.audio.domain.model.Audio

interface FileManager {
    fun getAudiosFiles():List<Audio>
}