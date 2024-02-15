package com.example.sleepmeditation.features.audio.presentation.activity

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.sleepmeditation.databinding.ActivityAudioPlayerBinding
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.utils.Constants
import com.example.sleepmeditation.utils.Extensions.parcelable
import com.za.filemanagerapp.utils.managers.AudioManager
import com.za.filemanagerapp.utils.managers.AudioManagerImpl
import javax.inject.Inject

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAudioPlayerBinding
    private var audio: Audio? = null
    private var currentAudioPosition = 0
    private var audioList:ArrayList<Audio> = arrayListOf()
    private lateinit var audioPlayer: ExoPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        initAudioManager()
    }
    private fun getIntentData() {
        audio = intent.parcelable(Constants.AUDIO)
        audioList = intent.getParcelableArrayListExtra(Constants.AUDIO_LIST)!!
        currentAudioPosition = audioList.indexOf(audio)
    }

    private fun initAudioManager() {
        audioPlayer = ExoPlayer.Builder(this).build()
        binding.exoPlayer.player = audioPlayer
        audioPlayer.prepare()
        audioList.forEach { audio ->
            val mediaItem = audio.artUri?.let { MediaItem.fromUri(it) }
            mediaItem?.let { audioPlayer.addMediaItem(mediaItem) }
        }

        audioPlayer.seekTo(currentAudioPosition,0)
//        val mediaItem = audio?.artUri?.let { MediaItem.fromUri(it) }
//        mediaItem?.let { audioPlayer.setMediaItem(it) }
        audioPlayer.play()
    }

    override fun onStop() {
        super.onStop()
//        audioPlayer.release()
//        audioPlayer.stop()
    }
}