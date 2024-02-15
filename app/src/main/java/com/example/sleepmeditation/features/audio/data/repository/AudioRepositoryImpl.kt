package com.example.sleepmeditation.features.audio.data.repository

import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.features.audio.domain.repository.AudioRepository
import com.za.filemanagerapp.utils.managers.FileManager
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(private val fileManager: FileManager):
    AudioRepository {
    override fun getAudioFiles(): List<Audio> {
        return fileManager.getAudiosFiles()
    }
}