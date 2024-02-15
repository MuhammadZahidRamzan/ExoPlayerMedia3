package com.example.sleepmeditation.features.audio.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.features.audio.domain.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: AudioRepository
) :
    ViewModel() {
    private var _audiosList = MutableLiveData<List<Audio>>()
    val audiosList: LiveData<List<Audio>>
        get() = _audiosList

    init {
        getAudiosList()
    }

    private fun getAudiosList() {
        viewModelScope.launch(Dispatchers.IO) {
            _audiosList.postValue(repository.getAudioFiles())
        }
    }
}