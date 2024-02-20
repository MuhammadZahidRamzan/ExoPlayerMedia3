package com.example.sleepmeditation.features.audio.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sleepmeditation.R
import com.example.sleepmeditation.databinding.FragmentAudioBinding
import com.example.sleepmeditation.features.audio.adapters.AudioAdapter
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.example.sleepmeditation.features.audio.presentation.activity.AudioPlayerActivity
import com.example.sleepmeditation.features.audio.presentation.viewmodel.AudioViewModel
import com.example.sleepmeditation.utils.Constants
import com.za.filemanagerapp.utils.managers.AudioManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioFragment : Fragment() {

    @Inject
    lateinit var audioManager : AudioManager
    private var audioList:ArrayList<Audio> = arrayListOf()
    private val viewModel: AudioViewModel by viewModels()
    private lateinit var binding: FragmentAudioBinding
    private lateinit var audioAdapter: AudioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAudiosList()
        viewModelObserver()
    }

    private fun viewModelObserver() {
        viewModel.audiosList.observe(viewLifecycleOwner) {
            populateRecycler(it)
            audioList.addAll(it)
            audioManager.setAudios(it)
        }
    }
    private fun populateRecycler(audioList: List<Audio>) {
        binding.pbAudio.visibility = View.GONE
        binding.tvAllAudios.visibility = View.VISIBLE
        binding.tvAllAudios.text = resources.getString(R.string.all_audios_string,audioList.size)
        binding.audioRecycler.setHasFixedSize(true)
        binding.audioRecycler.layoutManager = LinearLayoutManager(requireContext())
        audioAdapter = AudioAdapter(requireContext(), audioList){
            goToAudioPlayerActivity(it)
        }
        binding.audioRecycler.adapter = audioAdapter
    }

    private fun goToAudioPlayerActivity(audio:Audio){
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra(Constants.AUDIO,audio)
        intent.putParcelableArrayListExtra(Constants.AUDIO_LIST,audioList)
        startActivity(intent)
    }
}