package com.example.sleepmeditation.di

import android.content.Context
import com.example.sleepmeditation.features.audio.data.repository.AudioRepositoryImpl
import com.example.sleepmeditation.features.audio.domain.repository.AudioRepository
import com.za.filemanagerapp.utils.managers.AudioManagerImpl
import com.za.filemanagerapp.utils.managers.FileManager
import com.example.sleepmeditation.utils.managers.FileManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideAudioRepository( fileManager: FileManager): AudioRepository {
        return AudioRepositoryImpl(fileManager)
    }

    @Provides
    @Singleton
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return FileManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): com.za.filemanagerapp.utils.managers.AudioManager {
        return AudioManagerImpl(context)
    }

}