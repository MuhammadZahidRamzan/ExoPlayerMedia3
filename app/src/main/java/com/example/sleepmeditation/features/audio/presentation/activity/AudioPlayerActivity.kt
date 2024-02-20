package com.example.sleepmeditation.features.audio.presentation.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sleepmeditation.R
import com.example.sleepmeditation.databinding.ActivityAudioPlayerBinding
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.features.audio.presentation.viewmodel.AudioViewModel
import com.example.sleepmeditation.features.audio.presentation.viewmodel.UIEvent
import com.example.sleepmeditation.features.audio.presentation.viewmodel.UIState
import com.example.sleepmeditation.utils.Constants
import com.example.sleepmeditation.utils.Extensions.parcelable
import com.example.sleepmeditation.utils.Utils
import com.example.sleepmeditation.utils.service.SimpleMediaService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAudioPlayerBinding
    private var audio: Audio? = null
    private var currentAudioPosition = 0
    private var audioList:ArrayList<Audio> = arrayListOf()
    private lateinit var viewModel: AudioViewModel
  //  private lateinit var audioPlayer: ExoPlayer
    private var isServiceRunning = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[AudioViewModel::class.java]
        getIntentData()
        when (viewModel.uiState.value) {
            UIState.Initial -> Toast.makeText(this,"Not Ready",Toast.LENGTH_SHORT).show()
            UIState.Ready -> {
                Toast.makeText(this, "Ready", Toast.LENGTH_SHORT).show()
            }
        }
        clickListeners()
        observers()
    }

    private fun observers() {
        viewModel.isPlaying.observe(this){
            if (it){
                binding.btnPlayPause.setIconResource(R.drawable.icon_pause)
            }else
                binding.btnPlayPause.setIconResource(R.drawable.icon_play)
        }
        viewModel.progressString.observe(this){
            binding.tvSeekBarStart.text = it
        //    binding.sbAudio.progress = it.toInt()
        }
        viewModel.duration.observe(this){
            binding.tvSeekBarEnd.text = Utils.formatDuration(it)
        }
        viewModel.currentMediaItemIndex.observe(this){
            if (it != null){
                audio = audioList[it]
            }
            updateUi()
        }

    }




    private fun clickListeners() {
        binding.btnPlayPause.setOnClickListener {
            viewModel.onUIEvent(UIEvent.PlayPause)

        }
        binding.btnNext.setOnClickListener {
            viewModel.seekToNextPrevious(true)
        }
        binding.btnPrev.setOnClickListener {
            viewModel.seekToNextPrevious(false)
        }
        binding.sbAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.onUIEvent(UIEvent.UpdateProgress(progress.toFloat()))
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIntentData() {
        audio = intent.parcelable(Constants.AUDIO)
        updateUi()
        audioList = intent.getParcelableArrayListExtra(Constants.AUDIO_LIST)!!
        currentAudioPosition = audioList.indexOf(audio)
        viewModel.seekTo(currentAudioPosition)
        viewModel.onUIEvent(UIEvent.SeekTo(currentAudioPosition))
        startService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startService() {
        if (!isServiceRunning) {
            val intent = Intent(this, SimpleMediaService::class.java)
            startForegroundService(intent)
            isServiceRunning = true
        }
    }

    private fun updateUi() {
        binding.tvAudioName.text = audio?.title
        Glide.with(this).load(audio?.image)
            .apply(RequestOptions().placeholder(R.mipmap.ic_launcher).centerCrop())
            .into(binding.ivAudioImage)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, SimpleMediaService::class.java))
        isServiceRunning = false
    }

//    private fun initAudioManager() {
//        audioPlayer = ExoPlayer.Builder(this).build()
//        binding.exoPlayer.player = audioPlayer
//        audioPlayer.prepare()
//        audioList.forEach { audio ->
//            val mediaItem = audio.artUri?.let { MediaItem.fromUri(it) }
//            mediaItem?.let { audioPlayer.addMediaItem(mediaItem) }
//        }
//
//        audioPlayer.seekTo(currentAudioPosition,0)
////        val mediaItem = audio?.artUri?.let { MediaItem.fromUri(it) }
////        mediaItem?.let { audioPlayer.setMediaItem(it) }
//        audioPlayer.play()
//    }

}