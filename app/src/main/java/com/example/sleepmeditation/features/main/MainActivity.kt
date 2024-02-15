package com.example.sleepmeditation.features.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sleepmeditation.R
import com.example.sleepmeditation.databinding.ActivityMainBinding
import com.example.sleepmeditation.features.audio.presentation.fragments.AudioFragment
import com.example.sleepmeditation.utils.Extensions.gone
import com.example.sleepmeditation.utils.Extensions.visible
import com.example.sleepmeditation.utils.permissions.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionManager: PermissionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initClicks()
    }

    private fun init() {
        permissionManager = PermissionManager(this) {
            if (it) {
                loadFragment(AudioFragment())
                binding.flFragment.visible()
                binding.btnAllowPermission.gone()
            } else {
                binding.flFragment.gone()
                binding.btnAllowPermission.visible()
            }
        }
        permissionManager.requestReadExternalStoragePermission()
    }

    private fun initClicks(){
        binding.btnAllowPermission.setOnClickListener {
            permissionManager.requestReadExternalStoragePermission()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the content of the FrameLayout with the fragment
        fragmentTransaction.replace(R.id.fl_fragment, fragment)

        // Optional: Add the transaction to the back stack (for back navigation)
        // fragmentTransaction.addToBackStack(null)

        fragmentTransaction.commit()
    }
}